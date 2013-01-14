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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.xul;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerUiPlugin;
import org.pentaho.reporting.designer.core.ReportDesignerUiPluginRegistry;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulMenu;
import org.pentaho.ui.xul.containers.XulMenupopup;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;

public class XulDesignerFrame
{
  private static final String DIALOG_DEFINITION_FILE = "org/pentaho/reporting/designer/core/xul/designer-frame.xul"; //$NON-NLS-1$
  private XulWindow window;
  private ReportDesignerContext reportDesignerContext;

  public XulDesignerFrame() throws XulException
  {
    final ActionSwingXulLoader loader = new ActionSwingXulLoader();

    final ReportDesignerUiPlugin[] plugins = ReportDesignerUiPluginRegistry.getInstance().getPlugins();
    for (int i = 0; i < plugins.length; i++)
    {
      final ReportDesignerUiPlugin plugin = plugins[i];
      final Map<String, String> map = plugin.getXulAdditionalHandlers();
      final Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();
      while (entryIterator.hasNext())
      {
        final Map.Entry<String, String> entry = entryIterator.next();
        loader.register(entry.getKey(), entry.getValue());
      }
    }

    final XulDomContainer container = loader.loadXul(DIALOG_DEFINITION_FILE);
    final Document documentRoot = container.getDocumentRoot();

    for (int i = 0; i < plugins.length; i++)
    {
      final ReportDesignerUiPlugin plugin = plugins[i];
      final String[] strings = plugin.getOverlaySources();
      for (int j = 0; j < strings.length; j++)
      {
        final String source = strings[j];
        documentRoot.addOverlay(source);
      }
    }
    for (int i = 0; i < plugins.length; i++)
    {
      final ReportDesignerUiPlugin plugin = plugins[i];
      final XulEventHandler[] xulEventHandlers = plugin.createEventHandlers();
      for (int j = 0; j < xulEventHandlers.length; j++)
      {
        final XulEventHandler eventHandler = xulEventHandlers[j];
        container.addEventHandler(eventHandler);
      }
    }

    container.initialize();

    final XulComponent root = documentRoot.getRootElement();
    if (root instanceof XulWindow)
    {
      window = (XulWindow) root;
    }
    else
    {
      throw new XulException("Error getting Xul Database Dialog root, element of type: " + root);
    }

  }

  public XulWindow getWindow()
  {
    return window;
  }

  public JPopupMenu getPopupMenu(final String id)
  {
    final XulComponent mainMenuBar = window.getElementById(id);
    if (mainMenuBar == null)
    {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if (o instanceof JPopupMenu)
    {
      return (JPopupMenu) o;
    }
    return null;
  }

  public JMenuBar getMenuBar()
  {
    final XulComponent mainMenuBar = window.getElementById("main-menubar");//NON-NLS
    if (mainMenuBar == null)
    {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if (o instanceof JMenuBar)
    {
      return (JMenuBar) o;
    }
    return null;
  }

  public JMenuItem getMenuItemById(final String s)
  {
    final XulComponent mainMenuBar = window.getElementById(s);
    if (mainMenuBar == null)
    {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if (o instanceof JMenuItem)
    {
      return (JMenuItem) o;
    }
    return null;
  }

  public XulMenu getXulMenuById(final String s)
  {
    final XulComponent mainMenuBar = window.getElementById(s);
    if (mainMenuBar instanceof XulMenu == false)
    {
      return null;
    }
    return (XulMenu) mainMenuBar;
  }

  public XulComponent getXulComponentById(final String s)
  {
    final XulComponent mainMenuBar = window.getElementById(s);
    if (mainMenuBar == null)
    {
      return null;
    }
    return mainMenuBar;
  }

  public JMenu getMenuById(final String s)
  {
    final XulComponent mainMenuBar = window.getElementById(s);
    if (mainMenuBar == null)
    {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if (o instanceof JMenu)
    {
      return (JMenu) o;
    }
    return null;
  }

  public XulMenupopup getXulMenuPopupById(final String s)
  {
    final XulComponent mainMenuBar = window.getElementById(s);
    if (mainMenuBar instanceof XulMenupopup)
    {
      return (XulMenupopup) mainMenuBar;
    }
    return null;
  }

  public JComponent getToolBar(String id)
  {
    final XulComponent mainMenuBar = window.getElementById(id);
    if (mainMenuBar == null)
    {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if (o instanceof JComponent)
    {
      return (JComponent) o;
    }
    return null;
  }

  public void setReportDesignerContext(final ReportDesignerContext reportDesignerContext)
  {
    this.reportDesignerContext = reportDesignerContext;
    installContext(window);
  }

  public ReportDesignerContext getReportDesignerContext()
  {
    return reportDesignerContext;
  }

  private void installContext(final XulComponent component)
  {
    final List<XulComponent> xulComponents = component.getChildNodes();
    for (int i = 0; i < xulComponents.size(); i++)
    {
      final XulComponent child = xulComponents.get(i);
      if (child instanceof DesignerContextComponent)
      {
        final DesignerContextComponent asm = (DesignerContextComponent) child;
        asm.setReportDesignerContext(reportDesignerContext);
      }
      installContext(child);
    }
  }

  public ActionSwingMenuitem createMenu(final Action action)
  {
    final ActionSwingMenuitem item = new ActionSwingMenuitem(null, null, null, "menu-item");//NON-NLS
    item.setAction(action);
    return item;
  }
}