package org.aooshi.j.numberserver.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.aooshi.j.numberserver.client.NumberServerClient;
import org.aooshi.j.numberserver.service.IStoreService;
import org.aooshi.j.numberserver.service.SnowFlakeEService;
import org.aooshi.j.numberserver.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Home {

	private final static String SUCCESS = "ok";
	
	@Autowired
	private IStoreService service;

	private final SnowFlakeEService snowFlakeE = new SnowFlakeEService();

//	@Autowired
//	private NumberServerClient client;

	@GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String index() {

		String line = "\r\nDB: OK";
		try {
			List<Long> list = service.get("1");
			if (list == null || list.size() == 0) {
				line = "\r\nDB: No Found";
			}
		} catch (Exception exception) {
			line = "\r\nDB: Error";
		}

		line += "\r\n--";
		line += "\r\nSnowFlake.DataCenter: " + SnowFlake.instance.getConfiguration().getDataCenterId();
		line += "\r\nSnowFlake.Matchine: " + SnowFlake.instance.getConfiguration().getMachineId();

		line += "\r\n--";
		line += "\r\nSnowFlakeE15.Matchine: " + this.snowFlakeE.getSnowFlakeE15().getConfiguration().getMachineId();
		line += "\r\nSnowFlakeE16.Matchine: " + this.snowFlakeE.getSnowFlakeE16().getConfiguration().getMachineId();


//		for(int i=0;i<10;i++) {
//			Long snowflakeId = this.client.snowflake();
//			line += "\r\nSnowFlake.ID: " + snowflakeId;
//		}

//		line += "\r\n--";
//		line += "\r\nSnowFlakeE  .ID: " + this.snowFlakeE.getSnowFlakeE().nextId();
//		line += "\r\nSnowFlakeE15.ID: " + this.snowFlakeE.getSnowFlakeE15().nextId();
//		line += "\r\nSnowFlakeE16.ID: " + this.snowFlakeE.getSnowFlakeE16().nextId();

		line += "\r\n--";
		String n = "ID Server";
		String v = AppV.GetApplicationV(n, line);

		return v;
	}


	/**
	 * POST: 
	 * 	URL:    /add
	 * 	DATA:	id=00000&value=1
	 * 
	 *  HTTP 403
	 *  	: id exists
	 *  
	 * @param request
	 * @param response
	 * @param id
	 * @param value
	 * @return
	 */
	@ResponseBody
	@PostMapping("/add")
	public String add(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "value") Long value) {

		int actionCode = service.add(id, value);
		if (actionCode == ActionCode.OK)
		{
			return SUCCESS;
		}
		else if (actionCode == ActionCode.ID_EXISTS)
		{
			ControllerUtils.OutputNotfound(request, response);
			return "id exists";
		}
		else
		{
			ControllerUtils.OutputForbidden(request, response);
			return "action code " + actionCode;
		}
	}

	/**
	 * POST:
	 *  
	 * 	URL:   /update
	 * 	DATA:	id=00000&value=00000
	 *  
	 *  HTTP 404
	 *  	: id not exists
	 *  
	 * @param request
	 * @param response
	 * @param id
	 * @param value
	 * @return
	 */
	@ResponseBody
	@PostMapping("/update")
	public String update(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "value") Long value) {

		int actionCode = service.update(id, value);
		if (actionCode == ActionCode.OK)
		{
			return SUCCESS;
		}

		ControllerUtils.OutputForbidden(request, response);
		return "action code " + actionCode;
	}

	/**
	 * POST:
		 *  
		 * 	URL:   /delete
		 * 	DATA:	id=00000
		 * 
		 *  HTTP 404
		 *  	: not find
		 * 
	 * @return
	 */
	@ResponseBody
	@PostMapping("/delete")
	public String delete(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id) {

		int actionCode = service.delete(id);
		if (actionCode == ActionCode.OK)
		{
			return SUCCESS;
		}
		else if (actionCode == ActionCode.ID_NOT_EXISTS)
		{
			ControllerUtils.OutputNotfound(request, response);
			return "id not find";
		}

		ControllerUtils.OutputForbidden(request, response);
		return "action code " + actionCode;
	}
	
	/**
	 * GET:
	 * 	URL:   /get?id=0000
	 * 
	 * HTTP 404
	 * 		: Not Find
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/get")
	public String get(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id) {

		List<Long> list = service.get(id);
		if (list == null || list.size() == 0)
		{
			ControllerUtils.OutputNotfound(request, response);
			return "id not find";
		}

		String idstr = list.get(0).toString();
		return idstr;
	}

	/**
	 * GET: 
	 * 	URL:   /increment?id=0000&step=1
	 * 
	 * HTTP 404
	 * 		: Not Find
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/increment")
	public String increment(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "step") Integer step) {

		List<Long> list = service.increment(id, step);
		if (list == null || list.size() == 0)
		{
			ControllerUtils.OutputNotfound(request, response);
			return "id not find";
		}

		String idstr = list.get(0).toString();
		return idstr;
	}

	/**
	 * 
     * GET:
	 * 	URL:   /decrement?id=0000&step=1
	 * 
	 * HTTP 404
	 * 		: Not Find
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param step
	 * @return
	 */
	@ResponseBody
	@GetMapping("/decrement")
	public String decrement(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "step") Integer step) {

		List<Long> list = service.decrement(id, step);
		if (list == null || list.size() == 0)
		{
			ControllerUtils.OutputNotfound(request, response);
			return "id not find";
		}

		String idstr = list.get(0).toString();
		return idstr;
	}

	/**

	 * GET:
	 * 	URL:   /getOrAdd?id=0000&default=1
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param defaultValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/getOrAdd")
	public String getOrAdd(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "default") Long defaultValue) {

		Long v1 = service.getOrAdd(id, defaultValue);	

		String idstr = v1.toString();
		return idstr;
	}

	/**
	 * 
     * GET: 
	 * 	URL:   /incrementOrAdd?id=0000&step=1&default=1
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param step
	 * @param defaultValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/incrementOrAdd")
	public String incrementOrAdd(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "step") Integer step
			,@RequestParam(name = "default") Long defaultValue) {

		Long v1 = service.incrementOrAdd(id, step, defaultValue);	

		String idstr = v1.toString();
		return idstr;
	}

	/**
	 * GET:
	 * 	URL:   /decrementOrAdd?id=0000&step=1&default=1
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param step
	 * @param defaultValue
	 * @return
	 */
	@ResponseBody
	@GetMapping("/decrementOrAdd")
	public String decrementOrAdd(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(name = "id") String id
			,@RequestParam(name = "step") Integer step
			,@RequestParam(name = "default") Long defaultValue) {

		Long v1 = service.decrementOrAdd(id, step, defaultValue);	

		String idstr = v1.toString();
		return idstr;
	}

	/**
	 * GET:
	 * 	URL:   /snowflake
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping("/snowflake")
	public String snowflake(HttpServletRequest request,HttpServletResponse response) {
		long id = SnowFlake.instance.nextId();
		return "" + id;
	}

	/**
	 * GET:
	 * 	URL:   /snowflake15
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping("/snowflake15")
	public String snowflake15(HttpServletRequest request,HttpServletResponse response) {
		long id = this.snowFlakeE.getSnowFlakeE15().nextId();
		return "" + id;
	}

	/**
	 * GET:
	 * 	URL:   /snowflake16
	 *
	 * @return
	 */
	@ResponseBody
	@GetMapping("/snowflake16")
	public String snowflake16(HttpServletRequest request,HttpServletResponse response) {
		long id = this.snowFlakeE.getSnowFlakeE16().nextId();
		return "" + id;
	}
}
