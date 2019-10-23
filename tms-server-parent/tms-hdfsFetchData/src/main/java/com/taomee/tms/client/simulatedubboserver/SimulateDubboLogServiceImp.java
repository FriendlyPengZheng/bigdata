package com.taomee.tms.client.simulatedubboserver;

import com.taomee.tms.mgr.entity.ArtifactInfo;
/**
 * 
 * @author looper
 * @date 2017年5月18日 上午10:56:58
 * @project tms-hdfsFetchData SimulateDubboLogServiceImp
 */
public class SimulateDubboLogServiceImp implements SimulateDubboLogService {

	@Override
	public ArtifactInfo getArtifactInfoByTaskId(String taskId) {
		// TODO Auto-generated method stub
		// return null;
		/**
		 * @author looper
		 * @date 2017年5月18日 上午10:51:40
		 * @body_statement return null;
		 * 
		 */
		System.out.println("传递进来的taskId,"+taskId);
		/**
		 * 构造数据
		 */
		ArtifactInfo artifactInfo = new ArtifactInfo();
		artifactInfo.setOffset("d7");
		artifactInfo.setPeriod(0);
		artifactInfo.setHiveTableName("t_156_artifact");
		artifactInfo.setArtifactId(156);
		
		return artifactInfo;
	}

}
