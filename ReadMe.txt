使用方法说明：
主配置文件main/resources/config/config.xml
各项配置说明如下：
    jdbc-connection  配置数据库连接，目前只支持Oracle数据库。
    java-model-generator 配置生成的java代码。
        targetPackage 生成的model类的包名。生成的dto,dao类的包名是把targetPackage中的.model替换为.dao、.dto。
        targetProject 生成的Java文件的存放 路径。
    sqlmap-generator sqlmap配置文件的生成
        targetPackage  暂无使用
        targetProject 生成的配置文件的存放 路径
    dao-generator 暂无使用，但该项目必须存在 
        targetPackage 暂无使用
        targetProject 暂无使用
    table 配置需要生成的表
        tableName 表名
        primaryKey 主键，如果是联合 主键使用逗号分隔，不允许出现空格
        generated-key  子无素 
            sqlStatement 生成主键的SQL语句。
            例子：
        <table tableName="PRICE_STRATEGY" primaryKey="price_Strategy_Code">
            <generated-key sqlStatement="select seq_priceStrategyCode.nextVal from dual" />
        </table>
                    
     