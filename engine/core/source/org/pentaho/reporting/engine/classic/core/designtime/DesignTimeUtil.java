/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime;

import java.io.File;
import java.util.Date;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class DesignTimeUtil
{
  private DesignTimeUtil()
  {
  }

  public static File getContextAsFile(final AbstractReportDefinition reportDefinition)
  {
    final ResourceKey key = getContextKey(reportDefinition);
    return getContextAsFile(key);
  }

  public static File getContextAsFile(ResourceKey key)
  {
    while (key != null)
    {
      final Object identifier = key.getIdentifier();
      if (identifier instanceof File)
      {
        return (File) identifier;
      }

      key = key.getParent();
    }
    return null;
  }

  public static ResourceKey getContextKey(final AbstractReportDefinition reportDefinition)
  {
    AbstractReportDefinition e = reportDefinition;
    while (e != null)
    {
      final ResourceKey base = e.getContentBase();
      if (base != null)
      {
        return base;
      }
      final Section parentSection = e.getParentSection();
      if (parentSection != null)
      {
        final ReportDefinition reportDefinition1 = parentSection.getReportDefinition();
        if (reportDefinition1 instanceof AbstractReportDefinition)
        {
          e = (AbstractReportDefinition) reportDefinition1;
        }
        else
        {
          e = null;
        }
      }
      else
      {
        e = null;
      }
    }
    return null;
  }


  public static ResourceBundleFactory getResourceBundleFactory(final AbstractReportDefinition reportDefinition)
  {
    AbstractReportDefinition e = reportDefinition;
    while (e != null)
    {
      final ResourceBundleFactory base = e.getResourceBundleFactory();
      if (base != null)
      {
        return base;
      }
      final Section parentSection = e.getParentSection();
      if (parentSection != null)
      {
        final ReportDefinition reportDefinition1 = parentSection.getReportDefinition();
        if (reportDefinition1 instanceof AbstractReportDefinition)
        {
          e = (AbstractReportDefinition) reportDefinition1;
        }
        else
        {
          e = null;
        }
      }
      else
      {
        e = null;
      }
    }
    return null;
  }

  public static MasterReport getMasterReport(final Element element)
  {
    AbstractReportDefinition e = (AbstractReportDefinition) element.getReportDefinition();
    if (e instanceof MasterReport)
    {
      return (MasterReport) e;
    }

    while (e != null)
    {
      final Section parentSection = e.getParentSection();
      if (parentSection != null)
      {
        final ReportDefinition reportDefinition1 = parentSection.getReportDefinition();
        if (reportDefinition1 instanceof MasterReport)
        {
          return (MasterReport) reportDefinition1;
        }
        else if (reportDefinition1 instanceof AbstractReportDefinition)
        {
          e = (AbstractReportDefinition) reportDefinition1;
        }
        else
        {
          e = null;
        }
      }
      else
      {
        e = null;
      }
    }
    return null;
  }
  
  public static void resetTemplate(final MasterReport report)
  {
    resetDocumentMetaData(report);
    report.setContentBase(null);
  }

  public static void resetDocumentMetaData(final MasterReport report)
  {
    final DocumentMetaData metaData = report.getBundle().getMetaData();
    if (metaData instanceof WriteableDocumentMetaData)
    {
      final WriteableDocumentMetaData wmd = (WriteableDocumentMetaData) metaData;
      wmd.setBundleAttribute(ODFMetaAttributeNames.Meta.NAMESPACE,
          ODFMetaAttributeNames.Meta.INITIAL_CREATOR, wmd.getBundleAttribute
          (ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR));
      try
      {
        wmd.setBundleAttribute(ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.CREATOR, System.getProperty("user.name"));
      }
      catch (Exception e)
      {
        // ignore it, not that important ...
      }
      wmd.setBundleAttribute(ODFMetaAttributeNames.DublinCore.NAMESPACE,
          ODFMetaAttributeNames.DublinCore.DESCRIPTION, null);
      wmd.setBundleAttribute(ODFMetaAttributeNames.DublinCore.NAMESPACE,
          ODFMetaAttributeNames.DublinCore.SUBJECT, null);
      wmd.setBundleAttribute(ODFMetaAttributeNames.DublinCore.NAMESPACE,
          ODFMetaAttributeNames.DublinCore.TITLE, null);
      wmd.setBundleAttribute(ODFMetaAttributeNames.Meta.NAMESPACE,
          ODFMetaAttributeNames.Meta.CREATION_DATE, new Date());
      wmd.setBundleAttribute(ODFMetaAttributeNames.Meta.NAMESPACE,
          ODFMetaAttributeNames.Meta.KEYWORDS, null);
    }
  }
}