package system.explorer;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;

public class FolderExplorerView extends AbstractTreeExplorer {
    
    @Controller
    def controller;

    public void init() {
        def props = controller.workunit.workunit.properties;
        def root = [id: props.root, caption:controller.title];
        doInit( root );
    }
}

