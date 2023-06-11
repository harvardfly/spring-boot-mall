# 基于Spring boot + Mybatis + mybatis generator的项目

## 技术栈介绍
1. 通过mybatis generator 并配置 generatorConfig.xml 自动生成dao Mapper接口、pojo实体类对象、resource/mappers sql语句与dao的映射xml文件；
2. Application启动文件定义@MapperScan注解自动扫描dao Mapper接口与mappers xml文件的映射；
3. exception模块封装定义统一的异常类，便于返回error时的状态码和msg，当捕获的非业务定义的异常时抛出GlobalExceptionHandler 定义的系统异常；
4. common模块ApiRestResponse类封装定义统一的返回对象，包含：请求成功返回值和出错返回值，这里并没有按通常的http code返回，错误的情况http也是返回200；
    #### 请求成功返回值：
    ```
   {
       "status": 10000,
       "msg": "SUCCESS",
       "data": {
           "id": 16,
           "username": "abc",
           "createTime": "2023-06-03T16:01:01.000+0000",
       }
   }
    ```
    #### 请求失败返回值：
    ```
    {
        "status": 10002,
        "msg": "密码不能为空",
        "data": null
    }
    ```
5. 通过定义request目录（放请求参数及其校验，javax.validation包的方法），vo目录（放返回给前端的结构）；
6. 引入swagger自动生成文档:  
    a. pom引入swagger依赖，Application启动文件增加@EnableSwagger2注解；  
    b. 在config目录下新增SpringFoxConfig类，新增WebMvcConfig类实现WebMvcConfigurer接口（配置接口地址映射）；
7. filter目录是定义的一些过滤器（相当于golang的middleware），如果要让这些过滤器生效：  
    a. 这些过滤器需要实现servlet.Filter接口，重写它的doFilter方法（如AdminFilter就是在请求接口前校验是否是管理员）；  
    b. 在config目录下新增相应的过滤器配置类，如：AdminFilterConfig，它主要是配置这个过滤器作用在哪些接口（相当于middleware引入到具体接口上）；
8. 集成Redis：  
    a. pom引入redis依赖，application.properties配置redis信息，Application启动文件增加@EnableCaching注解；  
    b. 给需要缓存的方法加上@Cacheable(value = "存储的key值")；  
    c. 在config目录下新增缓存配置类CachingConfig，实现给缓存加过期时间；  
    d. 对于需要加缓存的类必须要序列化才行，这里要对返回值的实体类实现io.Serializable类的接口，如 public class CategoryVO implements Serializable；
9. 使用pagehelper分页插件实现分页：  
    a. pom引入pagehelper依赖；  
    b. 在需要分页的service方法中通过PageHelper.startPage启动分页，并通过PageInfo得到分页结果；
10. Mybatis使用<foreach>子标签批量处理（就是批量删除、查找、新增等），需要拼接批量处理的SQL语句，如 in批量更新：
    ```xml
      <update id="batchUpdateSellStatus">
        update imooc_mall_product
        set status=#{sellStatus}
        where id in
        <foreach collection="ids" close=")" item="id" open="(" separator=",">
          #{id}
        </foreach>
      </update>
    ```
11. 数据库事务，在需要使用事务的地方加上@Transactional(rollbackFor = Exception.class)；
12. 使用JWT Token权限校验，这里使用两种方式实现，自定义serverlet方式+AOP注解方式：  
    a. 并通过自定义过滤器实现对接口JWT鉴权；
    b. 使用AOP注解方式，使用@JwtAuth就可以实现接口token鉴权；
13. 统一异常返回，定义GlobalExceptionAdvice、ServerErrorException、UnifyMessage实现对ServerErrorException类异常的统一返回：  
    #### Token不合法返回值：
    ```json
    {
      "code": 401,
      "message": "Token 不合法"
    }
    ```
14. 生产部署：  
    a. maven Lifecycle clean清除内容；  
    b. maven Lifecycle package（其实 mvn install也可以打成jar，woa离线推送就是这样做的） 打成jar包（生成在target目录）；  
    c. （woa的操作）将生成的jar包COPY到 /app目录，ENTRYPOINT tini守护进程，启动：CMD ["su-exec","woa:woa","java", "-XX:MaxRAMPercentage=80.0","-jar", "/app/app.jar"]；
