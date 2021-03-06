package com.example.zuul.extend;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建时间:2019/04/18
 * 创建人:pmc
 * 描述:
 */
//自定义zuulfilter 实现鉴权、流量转发、请求统计等功能
/* filter生命周期
PRE： 这种过滤器在请求被路由之前调用。我们可利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等。
ROUTING：这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netfilx Ribbon请求微服务。
POST：这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTP Header、收集统计信息和指标、将响应从微服务发送给客户端等。
ERROR：在其他阶段发生错误时执行该过滤器。 除了默认的过滤器类型，Zuul还允许我们创建自定义的过滤器类型。例如，我们可以定制一种STATIC类型的过滤器，直接在Zuul中生成响应，而不将请求转发到后端的微服务。*/
public class CheckFilter extends ZuulFilter
{
    @Override
    public String filterType()
    {
        return "pre";////定义filter的类型，有pre、route、post、error四种
    }

    @Override
    public int filterOrder()
    {
        return 10; //定义filter的顺序，数字越小表示顺序越高，越先执行
    }

    @Override
    public boolean shouldFilter()
    {
        return true;//表示是否需要执行该filter，true表示执行，false表示不执行
    }

    @Override
    public Object run() throws ZuulException
    {//filter需要执行的具体操作
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        String token = httpServletRequest.getParameter("token");
        if (StringUtils.isNotBlank(token))
        {
            requestContext.setSendZuulResponse(true);//对请求进行路由
            requestContext.setResponseStatusCode(200);
            requestContext.set("success", true);
        } else
        {
            requestContext.setSendZuulResponse(false);//对请求进行路由
            requestContext.setResponseStatusCode(400);
            requestContext.getResponse().setContentType("text/html;charset=UTF-8");//防止中文乱码
            requestContext.setResponseBody("token 为空！无权限访问！");
            requestContext.set("success", false);
        }
        return null;
    }
}
