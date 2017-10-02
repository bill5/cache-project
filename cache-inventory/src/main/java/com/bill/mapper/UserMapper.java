package com.bill.mapper;

import java.util.List;
import java.util.Map;

public interface UserMapper {
	List<Map<String,String>> findUser();
}
