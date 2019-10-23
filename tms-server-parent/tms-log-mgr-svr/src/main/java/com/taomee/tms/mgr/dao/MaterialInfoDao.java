package com.taomee.tms.mgr.dao;


import com.taomee.tms.mgr.entity.MaterialInfo;

public interface MaterialInfoDao {
	Integer insertMaterialInfo(MaterialInfo materialInfo);
	void updateMaterialInfo(MaterialInfo materialInfo) ;
	void deleteMaterialInfo(Integer materialId) ;
	MaterialInfo getMaterialInfoBymaterialId(Integer materialId) ; 
	MaterialInfo getMaterialInfoBymaterialName(String materialName) ;
	void insertMaterialInfoBymaterialId(MaterialInfo materialInfo) ;
}
