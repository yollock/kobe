# 协议设计 @20161227

header 16字节

(2)(0-1)  : magic number
(4)(2-5)  : 框架版本
(2)(6-7)  : 从右向左,opaque,第1位,请求类型,request=0,response=1;第2-3位,响应类型,normal=00,01=void,exception=10;
            第4位,twoway=0,oneway=1;第5-7位,序列化类型,支持8种,Hissian=000,JSON=001;第8-9位,压缩类型,支持4种;
(8)(8-15) : 请求ID

body

1.服务接口名
2.调服务的方法名
3.调服务的方法参数描述符
4.调服务的方法参数值
5.attachments<String,String>,包含application,service_version,group等









