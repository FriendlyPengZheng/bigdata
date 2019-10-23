package com.taomee.tms.mgr.module;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.dubbo.config.annotation.Reference;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.GameInfo;
import com.taomee.tms.mgr.entity.Navi;

@Service
public class NavigatorService {

	private Map<String, String> aGameTypes;
	private String baseUrl = "";
	private Map<String, Object> navigatorAll;
	@Autowired
	private UserService userService;
	@Autowired
	private GameService gameService;
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletResponse response;

	public NavigatorService() {
		// ArrayList<String> gameTypes = gameService.getGameType();
		// this.aGameTypes = new HashMap<String,String>();
		// System.out.println(JSON.toJSONString(gameTypes));
		// for (String gameType : gameTypes) {
		// this.aGameTypes.put(gameType, "");
		// }
	}

	public Map<String, String> getaGameTypes() {
		if (this.aGameTypes == null) {
			ArrayList<String> gameTypes = gameService.getGameType();
			this.aGameTypes = new HashMap<String, String>();
			// System.out.println(JSON.toJSONString(gameTypes));
			for (String gameType : gameTypes) {
				this.aGameTypes.put(gameType, "");
			}
		}
		return aGameTypes;
	}

	public void setaGameTypes(Map<String, String> aGameTypes) {
		this.aGameTypes = aGameTypes;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getNavigator() throws IOException, IllegalArgumentException, IllegalAccessException {
		try {
			// if (this.navigatorAll != null){
			// return this.navigatorAll;
			// }
			Map<Integer, Object> completeNavigatorInfo = this.getCompleteNavigatorInfo();
			Map<String, Map<String, Object>> navigator = (Map<String, Map<String, Object>>) completeNavigatorInfo
					.get(1);
			Map<String, ArrayList<String>> navigatorUrls = (Map<String, ArrayList<String>>) completeNavigatorInfo
					.get(2);
			
			// no authority
			if (navigator.isEmpty()) {
				// redirect
				// todo
			}
			// check current route in navigator, if not exist, jump to the first
			// enabled
			String route = request.getServletPath();
			route = route.substring(1);
			// System.out.println(route);
			// route = "gameanalysis/mobilegame/overview/index/01";

			String tempFirstRoute = navigator.keySet().iterator().next();
			this.checkRoute(navigatorUrls, route, (String) navigator.get(tempFirstRoute).get("url"));
			// get current navigator tree
			ArrayList<String> currentMap = this.getCurrentMap(navigator, navigatorUrls, route);
			
			Map<String, Map<String, Object>> topBar = navigator;
			Map<String, Map<String, Object>> secondBar = null;
			Map<String, Map<String, Object>> aside = null;
			String gameFunction = "-1";
			
			if (currentMap.size() > 0 && topBar.get(currentMap.get(0)) != null) {
				if (topBar.get(currentMap.get(0)).get("children") != null) {
					secondBar = (Map<String, Map<String, Object>>) topBar.get(currentMap.get(0)).get("children");
				}
				// current url for game list
				if ("1".equals(topBar.get(currentMap.get(0)).get("gameRelated"))) {
					gameFunction = (String) topBar.get(currentMap.get(0)).get("funcSlot");
					this.setGameRoute(secondBar);
				}
			}
			if (currentMap.size() > 1) {
				if (secondBar.get(currentMap.get(1)).get("children") != null) {
					aside = (Map<String, Map<String, Object>>) secondBar.get(currentMap.get(1)).get("children");
				}
			}
			Map<String, String> currentPage = new HashMap<String, String>();
			int currentInt = currentMap.size();
			if (currentInt > 1) {
				String childKey = currentMap.get(currentInt - 1);
				String parentKey = currentMap.get(currentInt - 2);
				Map<String, Object> parentAside = null;
				if (aside.containsKey(parentKey)) {
					parentAside = aside.get(parentKey);
				} else if (secondBar.containsKey(parentKey)) {
					parentAside = secondBar.get(parentKey);
				}
				if (parentAside != null) {
					String parentKeyTemp = (String) parentAside.get("key");
					for (String gameType : this.getaGameTypes().keySet()) {
						parentKeyTemp.replace(this.getaGameTypes().get(gameType), "");
					}
					currentPage.put("parent", parentKeyTemp);
					currentPage.put("parent_name", (String) parentAside.get("name"));
					if (parentAside.get("children") != null
							&& ((Map<String, Object>) parentAside.get("children")).containsKey(childKey)) {
						currentPage.put("child",
								(String) ((Map<String, Object>) ((Map<String, Object>) parentAside.get("children"))
										.get(childKey)).get("key"));
						currentPage.put("child_name",
								(String) ((Map<String, Object>) ((Map<String, Object>) parentAside.get("children"))
										.get(childKey)).get("name"));
					}
				}
			}
			boolean status = true;
			if (aside != null && !aside.isEmpty()) {
				for (String key : aside.keySet()) {
					status = status && ((String) aside.get(key).get("isParent") == "0");
				}
			}
			if (status) {
				aside = secondBar;
				secondBar = null;
			}
			this.navigatorAll = new HashMap<String, Object>();
			this.navigatorAll.put("top_bar", topBar);
			this.navigatorAll.put("second_bar", secondBar);
			
			ArrayList<Integer> tempFunc = new ArrayList<Integer>();
			tempFunc.add(Integer.parseInt(gameFunction));

			/*System.out.println("gameFunction"+ gameFunction);
			System.out.println("this.getGameNavigator(tempFunc):" +this.getGameNavigator(tempFunc));*/
			this.navigatorAll.put("game", "-1".equals(gameFunction) ? null : this.getGameNavigator(tempFunc));
			this.navigatorAll.put("aside", aside);
			this.navigatorAll.put("current_page", currentPage);
		} catch (NullPointerException e) {
			System.out.println("非法请求：" + request.getServletPath());
			e.printStackTrace();
		}
		return this.navigatorAll;
	}

	/*
	 * 设置游戏链接
	 */
	private void setGameRoute(Map<String, Map<String, Object>> navi) {
		for (String key : navi.keySet()) {
			Map<String, Object> iNavi = navi.get(key);
			if (this.getaGameTypes().containsKey(iNavi.get("key"))) {
				this.getaGameTypes().put((String) iNavi.get("key"), (String) iNavi.get("url"));
			}
		}
	}

	/*
	 * get game navigator if necessary
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected Map<Integer, Object> getGameNavigator(ArrayList<Integer> funcMask)
			throws IllegalArgumentException, IllegalAccessException {
		Map<Integer, GameInfo> aAuthGame = gameService.getIdGroupedGameByAuth(funcMask);
		Map<Integer, Object> rAuthGame = new HashMap<Integer, Object>();
		String sRoute = request.getServletPath().substring(1);
		String sGameType = this.getGameTypeFromRoute(sRoute);
		Map<String, ArrayList<String>> navigatorUrls = (Map<String, ArrayList<String>>) this.getCompleteNavigatorInfo()
				.get(2);
		String gameTypeRoute = null;
		for (int gameId : aAuthGame.keySet()) {
			gameTypeRoute = (sGameType == null ? sRoute : sRoute.replace(sGameType, aAuthGame.get(gameId).getGameType()));
			// GameInfo 对象不能动态加URL设置 这里转为 map
			Map<String, Object> gameInfoNew = new HashMap<String, Object>();
			Field[] declaredFields = aAuthGame.get(gameId).getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				field.setAccessible(true);
				gameInfoNew.put(field.getName(), field.get(aAuthGame.get(gameId)));
			}
			if (!navigatorUrls.containsKey(gameTypeRoute)) {
				gameInfoNew.put("url", this.getaGameTypes().get(aAuthGame.get(gameId).getGameType()) + "?game_id="
						+ aAuthGame.get(gameId).getGameId());
			} else {
				gameInfoNew.put("url",
						this.getBaseUrl() + "/" + gameTypeRoute + "?game_id=" + aAuthGame.get(gameId).getGameId());
			}
			rAuthGame.put(gameId, gameInfoNew);
		}
		return rAuthGame;
	}

	/*
	 * Get game type from the route given
	 */
	protected String getGameTypeFromRoute(String sRoute) {
		String gameType = null;
		for (String key : this.getaGameTypes().keySet()) {
			if (sRoute.indexOf(key) != -1) {
				gameType = key;
				break;
			}
		}
		return gameType;
	}

	/*
	 * check the route in navigator
	 */
	private void checkRoute(Map<String, ArrayList<String>> navigatorNewUrls, String route, String defaultRoute)
			throws IOException {
		// defaultRoute =
		// "http://localhost:8080/tms-log-mgr-web/gameanalysis/overview/index/01";
		// System.out.println(route);
		// System.out.println(defaultRoute);
		if (!navigatorNewUrls.containsKey(route)) {
			// redirect
			// System.out.println("fuck");
			// try {
			// request.getRequestDispatcher(defaultRoute).forward(request,
			// response);
			// } catch (ServletException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// response.sendRedirect(defaultRoute);
			// response.flushBuffer();
			// return;
		}
		// System.out.println(JSON.toJSONString(navigatorNewUrls));
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getCurrentMap(Map<String, Map<String, Object>> navigator,
			Map<String, ArrayList<String>> urls, String route) {
		/*System.out.println("route:" + route);
		System.out.println("urls:" + urls);*/
		ArrayList<String> currentMap = urls.get(route);
		for (String naviId : urls.get(route)) {
			if (navigator.containsKey(naviId)) {
				navigator.get(naviId).put("current", "1");
				if (!navigator.get(naviId).containsKey("children") || navigator.get(naviId).get("children") == null) {
					break;
				}
				navigator = (Map<String, Map<String, Object>>) navigator.get(naviId).get("children"); // 可能有问题
			}
		}
		return currentMap;
	}

	public Map<Integer, Object> getCompleteNavigatorInfo() {
		// todo add session
		Map<Integer, ArrayList<Map<String, Object>>> rawNavigator = this.getRawNavigator();
		String[] authIds = userService.getAuthority();
		if(authIds == null || authIds.length == 0) {
			return null;
		}
		Map<Integer, Object> navigatorInfo = this.parseRawNavigator(rawNavigator, authIds);
		return navigatorInfo;
	}

	private Map<Integer, ArrayList<Map<String, Object>>> getRawNavigator() {
		// TODO Auto-generated method stub
		// TODO : status 字段的处理
		List<Navi> naviInfo = logMgrService.getNaviInfosByLevel();
		Map<Integer, ArrayList<Map<String, Object>>> naviByLevel = new ConcurrentHashMap<Integer, ArrayList<Map<String, Object>>>();
		for (int i = 0; i < naviInfo.size(); i++) {
			Navi navi = naviInfo.get(i);
			Map<String, Object> naviRebuild = new HashMap<String, Object>();
			naviRebuild.put("naviId", Integer.toString(navi.getNaviId()));
			naviRebuild.put("name", navi.getNaviName());
			naviRebuild.put("key", navi.getNaviKey());
			naviRebuild.put("url", navi.getNaviUrl());
			naviRebuild.put("route", navi.getNaviUrl());
			naviRebuild.put("parentId", Integer.toString(navi.getParentId()));
			naviRebuild.put("authId", navi.getAuthId());
			naviRebuild.put("isPage", Integer.toString(navi.getIsPage()));
			naviRebuild.put("isMain", Integer.toString(navi.getIsMain()));
			naviRebuild.put("gameRelated", Integer.toString(navi.getGameRelated()));
			naviRebuild.put("isParent", Integer.toString(navi.getIsParent()));
			naviRebuild.put("funcSlot", Integer.toString(navi.getFuncSlot()));
			if (naviByLevel.containsKey(navi.getLevel())) {
				naviByLevel.get(navi.getLevel()).add(naviRebuild);
			} else {
				ArrayList<Map<String, Object>> tmpNaviList = new ArrayList<Map<String, Object>>();
				tmpNaviList.add(naviRebuild);
				naviByLevel.put(navi.getLevel(), tmpNaviList);
			}
		}
		return naviByLevel;
	}

	public String getBaseUrl() {
		if ("".equals(this.baseUrl)) {
			int index = request.getRequestURL().lastIndexOf(request.getServletPath());
			String substr = request.getRequestURL().substring(0, index);
			this.setBaseUrl(substr);
		}
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	private Map<Integer, Object> parseRawNavigator(Map<Integer, ArrayList<Map<String, Object>>> rawNavigator,
			String[] authIds) {
		int naviLevel = 1;
		Arrays.sort(authIds);
		Map<String, Map<String, Map<String, Map<String, Object>>>> navigatorInfo = new ConcurrentHashMap<String, Map<String, Map<String, Map<String, Object>>>>();
		Map<String, Map<String, ArrayList<Map<String, ArrayList<String>>>>> navigatorUrls = new HashMap<String, Map<String, ArrayList<Map<String, ArrayList<String>>>>>();
		while (rawNavigator.containsKey(naviLevel)) {
			// temp level
			Map<String, Map<String, Map<String, Object>>> levelNavies = new LinkedHashMap<String, Map<String, Map<String, Object>>>();
			for (Map<String, Object> navi : rawNavigator.get(naviLevel)) {
				if (Arrays.binarySearch(authIds, navi.get("authId")) == -1) {
					continue;
				}
				// navigator depends on all games
				if ("1".equals(navi.get("gameRelated"))) {
					ArrayList<Integer> funcMask = new ArrayList<Integer>();
					funcMask.add(Integer.valueOf((String) navi.get("funcSlot")));
					Map<String, ?> aAuthGame = gameService.getTypeGroupedGameByAuth(funcMask);
					if (null == aAuthGame || aAuthGame.isEmpty()) {
						continue;
					}
				}
				String tempParentId = (String) navi.get("parentId");
				if ("1".equals(navi.get("isPage"))) {
					if (!("0".equals(tempParentId))) {
						// store url with level
						int iTempLevel = naviLevel - 1;
						String tempLevel = Integer.toString(iTempLevel);

						Map<String, ArrayList<String>> tempIds = new ConcurrentHashMap<String, ArrayList<String>>();
						ArrayList<String> ids = new ArrayList<String>(5);
						ids.add((String) navi.get("naviId"));
						ArrayList<String> url = new ArrayList<String>(1);
						url.add((String) navi.get("url"));
						tempIds.put("ids", ids);
						tempIds.put("url", url);

						if (navigatorUrls.containsKey(tempLevel)) {
							if (navigatorUrls.get(tempLevel).containsKey(tempParentId)) {
								navigatorUrls.get(tempLevel).get(tempParentId).add(tempIds);
							} else {
								ArrayList<Map<String, ArrayList<String>>> tempParents = new ArrayList<Map<String, ArrayList<String>>>();
								tempParents.add(tempIds);
								navigatorUrls.get(tempLevel).put(tempParentId, tempParents);
							}
						} else {
							ArrayList<Map<String, ArrayList<String>>> tempParents = new ArrayList<Map<String, ArrayList<String>>>();
							tempParents.add(tempIds);
							Map<String, ArrayList<Map<String, ArrayList<String>>>> tempUrls = new ConcurrentHashMap<String, ArrayList<Map<String, ArrayList<String>>>>();
							tempUrls.put(tempParentId, tempParents);
							navigatorUrls.put(tempLevel, tempUrls);
						}
					} else {
						// first level store with url directly
						Map<String, ArrayList<Map<String, ArrayList<String>>>> tempFirstUrl = new HashMap<String, ArrayList<Map<String, ArrayList<String>>>>();
						tempFirstUrl.put((String) navi.get("naviId"), new ArrayList<Map<String, ArrayList<String>>>());
						navigatorUrls.put((String) navi.get("url"), tempFirstUrl);
					}
				}
				// rebuild url
				if (!("".equals(navi.get("url")))) {
					String fullUrl = this.getBaseUrl() + "/" + (String) navi.get("url");
					navi.put("url", fullUrl);
				}
				if (levelNavies.containsKey(tempParentId)) {
					levelNavies.get(tempParentId).put((String) navi.get("naviId"), navi);
				} else {
					Map<String, Map<String, Object>> tempLevelNavi = new LinkedHashMap<String, Map<String, Object>>();
					tempLevelNavi.put((String) navi.get("naviId"), navi);
					levelNavies.put(tempParentId, tempLevelNavi);
				}
			}
			if (levelNavies.isEmpty())
				break;
			navigatorInfo.put(Integer.toString(naviLevel), levelNavies);
			naviLevel++;
		}
		
		if (navigatorInfo.containsKey("1") && navigatorInfo.get("1") != null) {
			// continue;
		} else {
			return null;
		}
		
		Map<String, ArrayList<String>> navigatorNewUrls = this.stripUrl(navigatorInfo, navigatorUrls);
		Map<Integer, Object> allNaviInfo = new ConcurrentHashMap<>();
		allNaviInfo.put(1, navigatorInfo.get("1").get("0"));
		allNaviInfo.put(2, navigatorNewUrls);
		return allNaviInfo;
	}

	private Map<String, ArrayList<String>> stripUrl(
			Map<String, Map<String, Map<String, Map<String, Object>>>> navigatorInfo,
			Map<String, Map<String, ArrayList<Map<String, ArrayList<String>>>>> navigatorUrls) {
		int naviLevel = 4, lastLevel = 4, nextLevel = 0;
		Map<String, ArrayList<String>> navigatorNewUrls = new HashMap<String, ArrayList<String>>();
		while (naviLevel > 0) {
			nextLevel = naviLevel - 1;
			if (navigatorInfo.containsKey(Integer.toString(naviLevel))) {
				Iterator<Entry<String, Map<String, Map<String, Object>>>> iter = navigatorInfo
						.get(Integer.toString(naviLevel)).entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Map<String, Map<String, Object>>> entry = (Map.Entry<String, Map<String, Map<String, Object>>>) iter
							.next();
					String parentId = entry.getKey();
					Map<String, Map<String, Object>> navis = entry.getValue();
					Iterator<Entry<String, Map<String, Object>>> iterNavi = navis.entrySet().iterator();
					while (iterNavi.hasNext()) {
						Map.Entry<String, Map<String, Object>> entryNavi = iterNavi.next();
						String naviId = entryNavi.getKey();
						Map<String, Object> navi = entryNavi.getValue();
						Map<String, Map<String, Object>> childNavi = new HashMap<String, Map<String, Object>>();
						// parent check child
						if (navi.containsKey("isParent") && "1".equals(navi.get("isParent"))) {
							if (navigatorInfo.containsKey(Integer.toString(lastLevel))
									&& navigatorInfo.get(Integer.toString(lastLevel)).containsKey(naviId)
									&& navigatorInfo.get(Integer.toString(lastLevel)).get(naviId) != null) {
								childNavi = navigatorInfo.get(Integer.toString(lastLevel)).get(naviId);
							}
						}
						// rewrite parent url
						if ("".equals(
								navigatorInfo.get(Integer.toString(naviLevel)).get(parentId).get(naviId).get("url"))
								&& !childNavi.isEmpty()) {
							String tempChildKey = childNavi.keySet().iterator().next();
							navigatorInfo.get(Integer.toString(naviLevel)).get(parentId).get(naviId).put("url",
									childNavi.get(tempChildKey).get("url"));
						}
						navigatorInfo.get(Integer.toString(naviLevel)).get(parentId).get(naviId).put("children",
								childNavi);
						// get url mapping
						if (navigatorUrls.containsKey(Integer.toString(naviLevel))
								&& navigatorUrls.get(Integer.toString(naviLevel)).containsKey(naviId)) {
							for (Map<String, ArrayList<String>> value : navigatorUrls.get(Integer.toString(naviLevel))
									.get(naviId)) {
								if (value.containsKey("ids")) {
									value.get("ids").add(naviId);
								}
								if (navigatorUrls.containsKey(Integer.toString(nextLevel))
										&& navigatorUrls.get(Integer.toString(nextLevel)).containsKey(parentId)) {
									navigatorUrls.get(Integer.toString(nextLevel)).get(parentId).add(value);
								} else if (navigatorUrls.containsKey(Integer.toString(nextLevel))) {
									navigatorUrls.get(Integer.toString(nextLevel)).put(parentId,
											new ArrayList<Map<String, ArrayList<String>>>());
									navigatorUrls.get(Integer.toString(nextLevel)).get(parentId).add(value);
								} else {
									Map<String, ArrayList<Map<String, ArrayList<String>>>> tmpUrlMap = new HashMap<String, ArrayList<Map<String, ArrayList<String>>>>();
									tmpUrlMap.put(parentId, new ArrayList<Map<String, ArrayList<String>>>());
									tmpUrlMap.get(parentId).add(value);
									navigatorUrls.put(Integer.toString(nextLevel), tmpUrlMap);
								}
								if (nextLevel == 0) {
									java.util.Collections.reverse(value.get("ids"));
									if (navigatorNewUrls.containsKey(value.get("url").get(0))) {
										navigatorNewUrls.get(value.get("url").get(0)).addAll(value.get("ids"));
									} else {
										navigatorNewUrls.put(value.get("url").get(0), new ArrayList<String>());
										navigatorNewUrls.get(value.get("url").get(0)).addAll(value.get("ids"));
									}
								}
							}
						}
					}
				}
				navigatorUrls.remove(naviLevel);
			}
			lastLevel = naviLevel;
			naviLevel = nextLevel;
		}
		if(navigatorUrls != null && !navigatorUrls.isEmpty()) {
			for (String key : navigatorUrls.keySet()) {
				if(!StringUtils.isNumeric(key)) {
					for (String num : navigatorUrls.get(key).keySet()) {
						ArrayList<String> topIds = new ArrayList<String>();
						topIds.add(num);
						navigatorNewUrls.put(key, topIds);
					}
				}
			}
		}
		return navigatorNewUrls;
	}

	/*
	 * 获取导航用于模板管理
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getNavigatorForManage() {
		Map<Integer, Object> completeNavigatorInfo = this.getCompleteNavigatorInfo();
		if(completeNavigatorInfo == null) {
			return null;
		}
		LinkedHashMap<String, Map<String, Object>> navigator = (LinkedHashMap<String, Map<String, Object>>) completeNavigatorInfo
				.get(1);
		Map<String, ArrayList<String>> navigatorUrls = (Map<String, ArrayList<String>>) completeNavigatorInfo.get(2);
		Map<String, Integer> childrenKey = new HashMap<String, Integer>();
		Map<String, Map<String, Object>> pages = new HashMap<String, Map<String, Object>>();
		for (String url : navigatorUrls.keySet()) {
			Map<String, Map<String, Object>> tmpNavigator = navigator;
			Map<String, Object> pageTemp = new ConcurrentHashMap<String, Object>();
			List<Object> nameArray = new ArrayList<Object>();
			List<Object> keyArray = new ArrayList<Object>();
			pageTemp.put("url", url);
			pageTemp.put("name", nameArray);
			pageTemp.put("key", keyArray);
			pages.put(url, pageTemp);
			for (String key : navigatorUrls.get(url)) {
				if (tmpNavigator.containsKey(key)) {
					if (tmpNavigator.get(key).containsKey("key")) {
						if (!this.getaGameTypes().containsKey(tmpNavigator.get(key).get("key"))) {
							((ArrayList<Object>) pages.get(url).get("key")).add(tmpNavigator.get(key).get("key"));
						}
						((ArrayList<Object>) pages.get(url).get("name")).add(tmpNavigator.get(key).get("name"));
					}
					if (tmpNavigator.get(key).containsKey("children")) {
						tmpNavigator = (Map<String, Map<String, Object>>) tmpNavigator.get(key).get("children");
					}
				} else {
					break;
				}
			}
			// System.out.println(JSON.toJSONString(pages));
			if (pages.get(url).get("key") == null) {
				pages.remove(url);
			} else {
				String keyStr = "";
				for (String kTmp : ((ArrayList<String>) pages.get(url).get("key"))) {
					if ("".equals(keyStr) || "".equals(kTmp)) {
						keyStr = keyStr.concat(kTmp);
					} else {
						keyStr = keyStr.concat("." + kTmp);
					}
				}
				if (childrenKey.containsKey(keyStr)) {
					pages.remove(url);
				} else {
					String nameStr = "";
					for (String nTmp : ((ArrayList<String>) pages.get(url).get("name"))) {
						if ("".equals(nameStr) || "".equals(nTmp)) {
							nameStr = nameStr.concat(nTmp);
						} else {
							nameStr = nameStr.concat("-" + nTmp);
						}
					}
			
					pages.get(url).put("title", nameStr);
					pages.get(url).put("key", keyStr);
					childrenKey.put(keyStr, 1);
				}
			}
		}
		// remove all item without key
		Set<String> navigatorKeys = navigator.keySet();
		for (String key : navigatorKeys) {
			if (navigator.get(key).containsKey("children")) {
				Map<String, Object> navi = navigator.get(key);
				Map<String, Integer> aChildrenKey = new HashMap<String, Integer>();
				Set<String> naviChildKeys = new LinkedHashSet<String>();
				for(String nclk : ((Map<String, Object>) navi.get("children")).keySet()) {
					naviChildKeys.add(nclk);
				}
				for (String k : naviChildKeys) {
					Map<String, Object> child = (Map<String, Object>) ((Map<String, Object>) navi.get("children"))
							.get(k);
					if (this.getaGameTypes().containsKey(child.get("key"))) {
						Set<String> childChildKeys = ((Map<String, Object>) child.get("children")).keySet();
						for (String jStr : childChildKeys) {
							Map<String, Object> cObj = (Map<String, Object>) ((Map<String, Object>) child
									.get("children")).get(jStr);
							if (aChildrenKey.containsKey(cObj.get("key"))) {
								continue;
							}
							((Map<String, Object>) navigator.get(key).get("children")).put(jStr, cObj);
							aChildrenKey.put((String) cObj.get("key"), 1);
						}
						((LinkedHashMap<String, Object>) navigator.get(key).get("children")).remove(k);
					} else if (!child.containsKey("key") || child.get("key") == null || "".equals(child.get("key"))) {
						((LinkedHashMap<String, Object>) navigator.get(key).get("children")).remove(k);
					}
				}
			}
			if (!navigator.get(key).containsKey("key") || ((navigator.get(key).get("key") == null
					|| "".equals(navigator.get(key).get("key")))
					&& (!navigator.get(key).containsKey("children") || navigator.get(key).get("children") == null))) {
				navigator.remove(key);
			}
		}
		Map<String, Object> manageNavi = new HashMap<String, Object>();
		manageNavi.put("navigator", navigator);
		manageNavi.put("pages", pages);
		return manageNavi;
	}
}
