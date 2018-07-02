Steps to setup mod_auth_open_id connect.

1) Install plain tomcat on the machine

2) Intall Apache2 on the machine. 

3) Download mod_jk connector and try to place in modules, need to create some properties and add it in httpd.conf 

4) at this apache2 tomcat should be talking.

Based on the configuration all request to tomcat should forwarded through apche2

5) Install mod_auth_openidc on linux --> there is a package available which installs all the necessary stuff even httpd aapche so we can try install it first.  and copy the needed so modules.

6) All the configuration needed to be done or in httpd.conf file

attached a sample copy of it for reference'




useful commands

 tail -200f /etc/httpd/logs/uno.sso-poc.com-access_log
 
 tail -200f /etc/httpd/logs/uno.sso-poc.com-error_log
 
 /usr/sbin/apachectl start
 
 /usr/sbin/apachectl stop
 
 /usr/sbin/apachectl status
 
 http://ashpicljas-d02.advisory.com/GoogleAPIAuthentication/ -- > Working Url
 
 http://ashpicljas-d02.advisory.com/GoogleAPIAuthentication/welcomeHome.do --> Second Level Authentication. 
 
 http://ashpicljas-d02.advisory.com/GoogleAPIAuthentication --> Error Url
 
