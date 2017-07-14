package com.nieyue.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nieyue.bean.Acount;
import com.nieyue.bean.AcountDTO;
import com.nieyue.bean.Finance;
import com.nieyue.bean.Role;
import com.nieyue.comments.IPCountUtil;
import com.nieyue.service.AcountService;
import com.nieyue.service.FinanceService;
import com.nieyue.service.RoleService;
import com.nieyue.util.DateUtil;
import com.nieyue.util.MyDESutil;
import com.nieyue.util.MyValidator;
import com.nieyue.util.ResultUtil;
import com.nieyue.util.StateResult;
import com.nieyue.util.StateResultList;
import com.yayao.messageinterface.SMSInterface;

import net.sf.json.JSONObject;


/**
 * 账户控制类
 * @author yy
 *
 */
@RestController
@RequestMapping("/acount")
public class AcountController {
	@Resource
	private AcountService acountService;
	@Resource
	private RoleService roleService;
	@Resource
	private FinanceService financeService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private SMSInterface sMSInterface;
	/**
	 * 账户分页浏览合伙人数目排行榜
	 * @param orderName 商品排序数据库字段
	 * @param orderWay 商品排序方法 asc升序 desc降序
	 * @return
	 */
	@RequestMapping(value = "/listGroupByMasterId", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList browsePagingAcountByMasterId(
			@RequestParam(value="masterId",required=false)Integer masterId,
			@RequestParam(value="pageNum",defaultValue="1",required=false)int pageNum,
			@RequestParam(value="pageSize",defaultValue="10",required=false) int pageSize,
			@RequestParam(value="orderName",required=false,defaultValue="apprenticeNum") String orderName,
			@RequestParam(value="orderWay",required=false,defaultValue="desc") String orderWay,HttpSession session)  {
		List<AcountDTO> list = new ArrayList<AcountDTO>();
		list= acountService.browsePagingAcountByMasterId(masterId,pageNum, pageSize, orderName, orderWay);
		if(list.size()>0){
			return ResultUtil.getSlefSRSuccessList(list);
			
		}else{
			return ResultUtil.getSlefSRFailList(list);
		}
	}
	/**
	 * 账户分页浏览
	 * @param orderName 商品排序数据库字段
	 * @param orderWay 商品排序方法 asc升序 desc降序
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList browsePagingAcount(
			@RequestParam(value="nickname",required=false)String nickname,
			@RequestParam(value="minScale",required=false)Double minScale,
			@RequestParam(value="maxScale",required=false)Double maxScale,
			@RequestParam(value="masterId",required=false)Integer masterId,
			@RequestParam(value="roleId",required=false)Integer roleId,
			@RequestParam(value="status",required=false)Integer status,
			@RequestParam(value="pageNum",defaultValue="1",required=false)int pageNum,
			@RequestParam(value="pageSize",defaultValue="10",required=false) int pageSize,
			@RequestParam(value="orderName",required=false,defaultValue="acount_id") String orderName,
			@RequestParam(value="orderWay",required=false,defaultValue="desc") String orderWay,HttpSession session)  {
			List<Acount> list = new ArrayList<Acount>();
			list= acountService.browsePagingAcount(nickname,minScale,maxScale,masterId,roleId,status,pageNum, pageSize, orderName, orderWay);
			if(list.size()>0){
				return ResultUtil.getSlefSRSuccessList(list);
				
			}else{
				return ResultUtil.getSlefSRFailList(list);
			}
	}
	/**
	 * 账户修改
	 * @return
	 */
	@RequestMapping(value = "/update", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResult updateAcount(@ModelAttribute Acount acount,HttpSession session)  {
		//账户已经存在
		if(acountService.loginAcount(acount.getPhone(), null,acount.getAcountId())!=null
				//||acountService.loginAcount(acount.getEmail(), null,acount.getAcountId())!=null
				){
			return ResultUtil.getSR(false);
		}
		//System.err.println(acount);
		boolean um = acountService.updateAcount(acount);
		//System.err.println(um);
		return ResultUtil.getSR(um);
	}
	/**
	 * 账户修改真实姓名、微信号、支付宝账号
	 * @return
	 */
	@RequestMapping(value = "/updateAcount", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResult updatePhoneAcount(@ModelAttribute Acount acount,HttpSession session)  {
		Acount newa = acountService.loadAcount(acount.getAcountId());
		if(acount.getWechat()!=null && !acount.getWechat().equals("")){
			newa.setWechat(acount.getWechat());
		}
		if(acount.getRealname()!=null && !acount.getRealname().equals("")){
			newa.setRealname(acount.getRealname());
		}
		if(acount.getAlipay()!=null && !acount.getAlipay().equals("")){
			newa.setAlipay(acount.getAlipay());
		}
		boolean um = acountService.updateAcount(newa);
		return ResultUtil.getSR(um);
	}
	/**
	 * 账户增加
	 * @return 
	 */
	@RequestMapping(value = "/add", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResult addAcount(@ModelAttribute Acount acount, HttpSession session) {
		//账户已经存在
		if(acountService.loginAcount(acount.getPhone(), null,null)!=null ||
				acountService.loginAcount(acount.getEmail(), null,null)!=null){
			return ResultUtil.getSR(false);
		}
		acount.setPassword(MyDESutil.getMD5(acount.getPassword()));
		boolean am = acountService.addAcount(acount);
		return ResultUtil.getSR(am);
	}
	/**
	 * 账户删除
	 * @return
	 */
	@RequestMapping(value = "/{acountId}/delete", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResult delAcount(@PathVariable("acountId") Integer acountId,HttpSession session)  {
		boolean dm = acountService.delAcount(acountId);
		return ResultUtil.getSR(dm);
	}
	/**
	 * 账户浏览数量
	 * @return
	 */
	@RequestMapping(value = "/count", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody int countAll(
			@RequestParam(value="nickname",required=false)String nickname,
			@RequestParam(value="minScale",required=false)Double minScale,
			@RequestParam(value="maxScale",required=false)Double maxScale,
			@RequestParam(value="masterId",required=false)Integer masterId,
			@RequestParam(value="roleId",required=false)Integer roleId,
			@RequestParam(value="status",required=false)Integer status,
			HttpSession session)  {
		int count = acountService.countAll(nickname,minScale,maxScale,masterId,roleId,status);
		return count;
	}
	/**
	 * 账户单个加载
	 * @return
	 */
	@RequestMapping(value = "/{acountId}", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList loadAcount(@PathVariable("acountId") Integer acountId,HttpSession session)  {
		List<Acount> list = new ArrayList<Acount>();
		Acount acount = acountService.loadAcount(acountId);
		if(acount!=null &&!acount.equals("")){
				list.add(acount);
				return ResultUtil.getSlefSRSuccessList(list);
			}else{
				return ResultUtil.getSlefSRFailList(list);
			}
	}
	/**
	 * 管理员登录
	 * @return
	 */
	@RequestMapping(value = "/login", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList loginAcount(
			@RequestParam("adminName") String adminName,
			@RequestParam("password") String password,
			@RequestParam(value="random",required=false) String random,
			HttpSession session)  {
		//1代验证码
		/*String ran= (String) session.getAttribute("random");
		List<Acount> list = new ArrayList<Acount>();
		if(!ran.equals(random)){
			return ResultUtil.getSlefSRFailList(list);
		}*/
		Integer gtResult = (Integer) session.getAttribute("gtResult");
		List<Acount> list = new ArrayList<Acount>();
		if(gtResult!=1){
			return ResultUtil.getSlefSRFailList(list);
		}
		Acount acount = acountService.loginAcount(adminName, MyDESutil.getMD5(password),null);
		if(acount!=null&&!acount.equals("")&&acount.getStatus().equals(0)){
			acount.setLoginDate(new Date());
			boolean b = acountService.updateAcount(acount);
			if(b){
			session.setAttribute("acount", acount);
			Integer roleId = acount.getRoleId();
			Role r = roleService.loadRole(roleId);
			session.setAttribute("role", r);
			List<Finance> f = financeService.browsePagingFinance(acount.getAcountId(), 1, 1, "finance_id", "asc");
			session.setAttribute("finance", f.get(0));
			list.add(acount);
			return ResultUtil.getSlefSRSuccessList(list);
			}
		}else if(acount.getStatus().equals(1)){
			List<String> l1 = new ArrayList<String>();
			l1.add("账户锁定");
			return ResultUtil.getSlefSRFailList(l1);
		}else if(acount.getStatus().equals(2)){
			List<String> l2 = new ArrayList<String>();
			l2.add("账户警告");
			return ResultUtil.getSlefSRSuccessList(l2);
		}
		return ResultUtil.getSlefSRFailList(list);
	}
	/**
	 * 手机验证码发送
	 * 
	 * @param adminName
	 * @param session
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/validCode", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody
	StateResultList validCode(@RequestParam("adminName") final String adminName,HttpSession session)  {
		List<String> l=new ArrayList<String>();
		//账户已经存在
				if(acountService.loginAcount(adminName, null,null)!=null){
					return ResultUtil.getSlefSRFailList(l);
				}
		String uvc="";
		String uvce="";
		
		if(Pattern.matches(MyValidator.REGEX_PHONE,adminName)){
			uvc="phoneValidCode";
			uvce="phoneValidCodeExpire";
		}
		if(session.getAttribute(uvce)!=null){
			String sessionvce = session.getAttribute(uvce).toString();
			try {
				if(!(new Date().after(DateUtil.getFirstToSecondsTime(DateUtil.parseDate(sessionvce), 1)))){//没超过一分钟
					l.add("请求过快");
					return ResultUtil.getSlefSRFailList(l);
					}
			} catch (ParseException e) {
				l.add("解析错误");
				return ResultUtil.getSlefSRFailList(l);
			}
			
		}	
		Integer userValidCode=(int) (Math.random()*9000)+1000;
		if(Pattern.matches(MyValidator.REGEX_PHONE,adminName)){
			 sMSInterface.SmsNumSend(String.valueOf(userValidCode), adminName);
		
		}
		session.setAttribute("adminName",adminName);
		session.setAttribute(uvc,userValidCode);
		session.setAttribute(uvce,DateUtil.getCurrentTime());
		l.add(userValidCode.toString());
		return ResultUtil.getSlefSRSuccessList(l);
	}
	/**
	 * web用户登录
	 * @return
	 */
	@RequestMapping(value = "/weblogin", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList webLoginAcount(
			@RequestParam("adminName") String adminName,
			@RequestParam("password") String password,
			@RequestParam(value="random",required=false) String random,
			HttpSession session)  {
		//1代验证码
		String ran= (String) session.getAttribute("random");
		List<Acount> list = new ArrayList<Acount>();
		if(!ran.equals(random)){
			return ResultUtil.getSlefSRFailList(list);
		}
		
		Acount acount = acountService.loginAcount(adminName, MyDESutil.getMD5(password),null);
		if(acount!=null&&!acount.equals("")/*&&acount.getStatus().equals(0)*/){
			acount.setLoginDate(new Date());
			boolean b = acountService.updateAcount(acount);
			if(b){
			session.setAttribute("acount", acount);
			Integer roleId = acount.getRoleId();
			Role r = roleService.loadRole(roleId);
			session.setAttribute("role", r);
			List<Finance> f = financeService.browsePagingFinance(acount.getAcountId(), 1, 1, "finance_id", "asc");
			session.setAttribute("finance", f.get(0));
			list.add(acount);
			return ResultUtil.getSlefSRSuccessList(list);
			}
		}/*else if(acount.getStatus().equals(1)){
			List<String> l1 = new ArrayList<String>();
			l1.add("账户锁定");
			return ResultUtil.getSlefSRFailList(l1);
		}else if(acount.getStatus().equals(2)){
			List<String> l2 = new ArrayList<String>();
			l2.add("账户警告");
			return ResultUtil.getSlefSRSuccessList(l2);
		}*/
		return ResultUtil.getSlefSRFailList(list);
	}
	/**
	 * web用户注册
	 * @return
	 */
	@RequestMapping(value = "/webregister", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList webRegisterAcount(
			@RequestParam("adminName") String adminName,
			@RequestParam("password") String password,
			HttpServletRequest request,
			@RequestParam(value="appVersion",required=false) String appVersion,
			@RequestParam(value="validCode",required=false) String validCode,
			HttpSession session)  {
		//手机验证码
		Integer phoneValidCode= (Integer) session.getAttribute("phoneValidCode");
		List<Acount> list = new ArrayList<Acount>();
		if(!(phoneValidCode.toString()).equals(validCode)){
			return ResultUtil.getSlefSRFailList(list);
		}
		//新用户注册登录
				//获取masterId
				Integer bvomasterId=null;
				String ip=IPCountUtil.getIpAddr(request);
				Acount acount=new Acount();
				
				if(ip!=null&&!ip.equals("")&&appVersion!=null&&!appVersion.equals("")){
					String appVersionSub=appVersion.substring(0,appVersion.indexOf("AppleWebKit")-1);//默认苹果
					if(appVersionSub.indexOf("Android")>-1){
						if(appVersionSub.indexOf("; wv)")<=-1){
							appVersionSub=appVersion.substring(0,appVersion.indexOf(")")-1);
						}else{
							appVersionSub=appVersion.substring(0,appVersion.indexOf("; wv)")-1);
						}
					}
					String ipappkey=MyDESutil.getMD5(ip+appVersionSub);
					BoundValueOperations<String, String> bvo = stringRedisTemplate.boundValueOps(ipappkey);
					if(bvo.size()>0){//有师傅
						bvomasterId=Integer.valueOf(bvo.get());
						bvo.expire(1, TimeUnit.MINUTES);
					}
				}
					acount.setScale(0.3);
					acount.setPhone(adminName);
					acount.setNickname(adminName);
					acount.setPassword(MyDESutil.getMD5(password));
					acount.setMasterId(bvomasterId);
					acount.setLoginDate(new Date());
					acount.setRoleId(1003);
					acountService.addAcount(acount);
					
				acount.setLoginDate(new Date());
				acountService.updateAcount(acount);
				session.setAttribute("acount", acount);
				Role role = roleService.loadRole(acount.getRoleId());
				session.setAttribute("role", role);
				List<Finance> f = financeService.browsePagingFinance(acount.getAcountId(), 1, 1, "finance_id", "asc");
				System.out.println(f.get(0).toString());
				session.setAttribute("finance", f.get(0));
				list.add(acount);
				return ResultUtil.getSlefSRSuccessList(list);
	}
	/**
	 * 根据IP navigator.appVersion确定师傅关系
	 * @return
	 */
	@RequestMapping(value = "/addMasterId", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResult addAcountMasterId(
			@RequestParam(value="masterId")Integer masterId,
			HttpServletRequest request,
			@RequestParam(value="appVersion") String appVersion,
			HttpSession session)  {
		String ip=IPCountUtil.getIpAddr(request);
		String appVersionSub=appVersion.substring(0,appVersion.indexOf("AppleWebKit")-1);//默认苹果
		if(appVersionSub.indexOf("Android")>-1){
			if(appVersionSub.indexOf("; wv)")<=-1){
				appVersionSub=appVersion.substring(0,appVersion.indexOf(")")-1);
			}else{
				appVersionSub=appVersion.substring(0,appVersion.indexOf("; wv)")-1);
			}
		}
		//设置masterId
		String ipappkey=MyDESutil.getMD5(ip+appVersionSub);//转码
		
		BoundValueOperations<String, String> bvo = stringRedisTemplate.boundValueOps(ipappkey);
		bvo.getAndSet(masterId.toString());
		return ResultUtil.getSuccess();
	}
	/**
	 * 微信登录
	 * @return
	 */
	@RequestMapping(value = "/wxlogin", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList WeixinBaseAcountLogin(
			@RequestParam("wxinfo") String wxinfo,
			HttpServletRequest request,
			@RequestParam(value="appVersion",required=false) String appVersion,
			HttpSession session)  {
			List<Object> list = new ArrayList<Object>();
			if(wxinfo==null||wxinfo.equals("")){
				
				return ResultUtil.getSlefSRFailList(list);
			}
		JSONObject jo = JSONObject.fromObject(wxinfo);
		String uuid = jo.getString("unionid");
		
		Acount acount = acountService.weixinBaseAcountLogin(uuid);
		//新用户注册登录
		if(acount==null||acount.equals("")){
		//获取masterId
		Integer bvomasterId=null;
		String ip=IPCountUtil.getIpAddr(request);
		
		if(ip!=null&&!ip.equals("")&&appVersion!=null&&!appVersion.equals("")){
			String appVersionSub=appVersion.substring(0,appVersion.indexOf("AppleWebKit")-1);//默认苹果
			if(appVersionSub.indexOf("Android")>-1){
				if(appVersionSub.indexOf("; wv)")<=-1){
					appVersionSub=appVersion.substring(0,appVersion.indexOf(")")-1);
				}else{
					appVersionSub=appVersion.substring(0,appVersion.indexOf("; wv)")-1);
				}
			}
			String ipappkey=MyDESutil.getMD5(ip+appVersionSub);
			BoundValueOperations<String, String> bvo = stringRedisTemplate.boundValueOps(ipappkey);
			if(bvo.size()>0){//有师傅
				bvomasterId=Integer.valueOf(bvo.get());
				bvo.expire(1, TimeUnit.MINUTES);
			}
		}
			acount=new Acount();
			acount.setScale(0.3);
			acount.setMasterId(bvomasterId);
			acount.setUuid(jo.getString("unionid"));
			acount.setSex(jo.getInt("sex"));
			acount.setNickname(jo.getString("nickname"));
			acount.setOpenid(jo.getString("openid"));
			acount.setIcon(jo.getString("headimgurl"));
			acount.setCountry(jo.getString("country"));
			acount.setProvince(jo.getString("province"));
			acount.setCity(jo.getString("city"));
			acount.setLoginDate(new Date());
			acount.setRoleId(1003);
			acountService.addAcount(acount);
		}
		acount.setLoginDate(new Date());
		acountService.updateAcount(acount);
		session.setAttribute("acount", acount);
		Role role = roleService.loadRole(acount.getRoleId());
		session.setAttribute("role", role);
		List<Finance> f = financeService.browsePagingFinance(acount.getAcountId(), 1, 1, "finance_id", "asc");
		System.out.println(f.get(0).toString());
		session.setAttribute("finance", f.get(0));
		list.add(acount);
		//刷新token
		/*String token = MyDESutil.getMD5(acount.getAcountId()+new Date().getTime());
		BoundValueOperations<String, String> bvo = stringRedisTemplate.boundValueOps(token);
		bvo.set(JSONObject.fromObject(acount).toString());
		JSONObject tokenJSON = new JSONObject();
		tokenJSON.put("token", bvo.getKey());
		list.add(tokenJSON);*/
		return ResultUtil.getSlefSRSuccessList(list);
	}
	/**
	 * 登录
	 * @return
	 */
	@RequestMapping(value = "/islogin", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList isLoginAcount(
			HttpSession session)  {
		Acount acount = (Acount) session.getAttribute("acount");
		List<Acount> list = new ArrayList<Acount>();
		if(acount!=null && !acount.equals("")){
			list.add(acount);
			return ResultUtil.getSlefSRSuccessList(list);
		}
		return ResultUtil.getSlefSRFailList(list);
	}
	/**
	 * 登出
	 * @return
	 */
	@RequestMapping(value = "/loginout", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody StateResultList loginoutAcount(
			HttpSession session)  {
		session.invalidate();
		return ResultUtil.getSlefSRSuccessList(null);
	}
	
}
