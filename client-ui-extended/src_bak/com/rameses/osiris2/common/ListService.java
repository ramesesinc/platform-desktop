package com.rameses.osiris2.common;

import java.util.List;
import java.util.Map;

public interface ListService {

    List<Map> getColumns(Map params);
    
    List getList(Map params);

}
