package func.domain;

import java.util.ArrayList;
import java.util.List;

public class Req {
	
	public ReqHead head;
	public ReqBody body;
	
	public String cookie;
	
	//要查询的日期，默认日期，从大到小排序
	public List<String> mapKey = new ArrayList<String>();
	
}
