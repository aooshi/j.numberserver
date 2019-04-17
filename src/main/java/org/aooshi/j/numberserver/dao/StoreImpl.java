package org.aooshi.j.numberserver.dao;

import java.util.Date;
import java.util.List;

import org.aooshi.j.numberserver.util.ActionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StoreImpl implements IStore {

    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public int add(long id, long value) {
		Date date = new Date();
        int rows = 0;
		try
		{
			rows = jdbcTemplate.update("insert into jnumberserver_store(id,v,createtime) values(?,?,?)",
              id,value,date);
		}
		catch(DuplicateKeyException e)
		{
			//String message = "id exists";
			return  ActionCode.ID_EXISTS;
		}
		
		if (rows > 0)
			return ActionCode.OK;
		
		return ActionCode.DB_EXECUTE_FAILURE;
	}

	@Override
	public int update(long id, long value) 
	{
		jdbcTemplate.update("update jnumberserver_store set v="+ value +" where id=" + id);
		return ActionCode.OK;
	}

	@Override
	public int delete(long id) 
	{
		int rows = 0;
		rows = jdbcTemplate.update("delete from jnumberserver_store where id=" + id);
		if (rows > 0)
			return ActionCode.OK;
		
		return ActionCode.ID_NOT_EXISTS;
	}

	@Override
	public List<Long> get(long id) {
		List<Long> list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1", Long.class);
		return list;
	}

	@Override
	@Transactional
	public List<Long> increment(long id, int step) {
		List<Long> list = null;
		
		int rows = jdbcTemplate.update("update jnumberserver_store set v=v+"+ step +" where id=" + id);
		if (rows == 0)
			return list;
		
		list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1", Long.class);
		 
		return list;
	}

	@Override
	@Transactional
	public List<Long> decrement(long id, int step) {

		List<Long> list = null;

		int rows = jdbcTemplate.update("update jnumberserver_store set v=v-"+ step +" where id=" + id);
		if (rows == 0)
			return list;
		
		list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1", Long.class);
		 
		return list;
	}

	@Override
	@Transactional
	public long incrementOrAdd(long id, int step, long defaultValue) {


		int rows = jdbcTemplate.update("update jnumberserver_store set v=v+"+ step +" where id=" + id);
		if (rows == 0)
		{
	        rows = jdbcTemplate.update("insert into jnumberserver_store(id,v,createtime) values(?,?,?)",
	              id,defaultValue,new Date());
		}
		
		List<Long> list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1", Long.class);
		return list.get(0);
	}

	@Override
	@Transactional
	public long decrementOrAdd(long id, int step, long defaultValue) {
		int rows = jdbcTemplate.update("update jnumberserver_store set v=v-"+ step +" where id=" + id);
		if (rows == 0)
		{
	        rows = jdbcTemplate.update("insert into jnumberserver_store(id,v,createtime) values(?,?,?)",
	              id,defaultValue,new Date());
		}
		
		List<Long> list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1 ", Long.class);
		return list.get(0);
	}

	@Override
	@Transactional
	public Long getOrAdd(long id, long defaultValue) {
		jdbcTemplate.update("update jnumberserver_store set v=v+0 where id=" + id);
		
		List<Long> list = jdbcTemplate.queryForList("select v from jnumberserver_store where id=" + id + " limit 1", Long.class);

		if (list == null || list.size() == 0)
		{
	        jdbcTemplate.update("insert into jnumberserver_store(id,v,createtime) values(?,?,?)",
	              id,defaultValue,new Date());

			return defaultValue;
		}
		else
		{
			return list.get(0);
		}
	}

}