package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.Component;

public interface ComponentDao {
	List<Component> getComponents(String moduleKey, Integer parentId, Integer gameId);
	Integer insertComponentInfo(Component componentInfo);
	void updateComponentInfo(Component componentInfo);
	void deleteComponentInfoBycomponentId(Integer componentId);
	List<Component> getComponentInfoByComponentId(Integer componentId);
	List<Component> getComponentInfoByParentId(Integer parentId);
	List<Component> getAllComponentsByIgnored(); 
}
