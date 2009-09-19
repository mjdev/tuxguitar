package org.herac.tuxguitar.cocoa;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.cocoa.menu.MacMenuPlugin;
import org.herac.tuxguitar.cocoa.opendoc.OpenDocPlugin;
import org.herac.tuxguitar.cocoa.toolbar.MacToolbarPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;

public class TGCocoaIntegrationPlugin extends TGPluginList {

	private List plugins; 
	
	protected List getPlugins() throws TGPluginException {
		if( this.plugins == null ){
			this.plugins = new ArrayList();
			
			this.plugins.add(new OpenDocPlugin());
			this.plugins.add(new MacMenuPlugin());
			this.plugins.add(new MacToolbarPlugin());
		}
		return this.plugins;
	}
}