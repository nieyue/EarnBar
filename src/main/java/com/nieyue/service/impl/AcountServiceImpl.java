package com.nieyue.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nieyue.bean.Acount;
import com.nieyue.bean.AcountDTO;
import com.nieyue.bean.Finance;
import com.nieyue.dao.AcountDao;
import com.nieyue.dao.FinanceDao;
import com.nieyue.service.AcountService;
@Service
public class AcountServiceImpl implements AcountService{
	@Resource
	AcountDao acountDao;
	@Resource
	FinanceDao financeDao;
	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public boolean addAcount(Acount acount) {
		Date date=new Date();
		acount.setCreateDate(date);
		acount.setLoginDate(date);
		acount.setStatus(0);
		boolean b = acountDao.addAcount(acount);
		Finance finance=new Finance();
		finance.setRecharge(0.0);
		finance.setConsume(0.0);
		finance.setWithdrawals(0.0);
		BoundValueOperations<String, String> etfubp = stringRedisTemplate.boundValueOps("EarnTurnFinanceUnitBaseProfit");//单个基准收益；
		Double unitBaseProfit=0.0;
		if(etfubp.get()!=null&&!etfubp.get().equals("")){
			unitBaseProfit=Double.valueOf(etfubp.get());
		}
		finance.setBaseProfit(unitBaseProfit);
		finance.setMoney(finance.getBaseProfit());//初始余额=基准收益+0.0
		finance.setUpdateDate(date);
		finance.setPartnerProfit(0.0);
		finance.setAcountId(acount.getAcountId());
		b=financeDao.addFinance(finance);
		return b;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public boolean delAcount(Integer acountId) {
		boolean b = acountDao.delAcount(acountId);
		return b;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public boolean updateAcount(Acount acount) {
		boolean b = acountDao.updateAcount(acount);
		return b;
	}

	@Override
	public Acount loadAcount(Integer acountId) {
		Acount r = acountDao.loadAcount(acountId);
		return r;
	}

	@Override
	public int countAll(
			String nickname,
			Double minScale,
			Double maxScale,
			Integer masterId,
			Integer roleId,
			Integer status) {
		int c = acountDao.countAll(nickname,
				minScale,
				maxScale, masterId, roleId,status);
		return c;
	}

	@Override
	public List<Acount> browsePagingAcount(String nickname,
			Double minScale,
			Double maxScale,Integer masterId,Integer roleId,Integer status,int pageNum, int pageSize,
			String orderName, String orderWay) {
		if(pageNum<1){
			pageNum=1;
		}
		if(pageSize<1){
			pageSize=0;//没有数据
		}
		List<Acount> l = acountDao.browsePagingAcount( nickname,
				minScale,
				 maxScale,masterId,roleId,status,pageNum-1, pageSize, orderName, orderWay);
		return l;
	}
	@Override
	public Acount loginAcount(String adminName, String password,Integer acountId) {
		Acount acount = acountDao.loginAcount(adminName, password, acountId);
		return acount;
	}
	@Override
	public Acount weixinBaseAcountLogin(String uuid) {
		Acount a = acountDao.weixinBaseAcountLogin(uuid);
		return a;
	}
	@Override
	public List<AcountDTO> browsePagingAcountByMasterId(Integer masterId, int pageNum, int pageSize, String orderName,
			String orderWay) {
		if(pageNum<1){
			pageNum=1;
		}
		if(pageSize<1){
			pageSize=0;//没有数据
		}
		List<AcountDTO> l = acountDao.browsePagingAcountByMasterId(masterId,pageNum-1, pageSize, orderName, orderWay);
		return l;
	}

	
}
