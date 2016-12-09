/**
 * 
 */
package sophia.game.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义插件的依赖
 * <p>
 * Copyright (c) 2013 by 游爱.
 * <p>
 * 通过插件定义的Annotation members， 使用依赖注入解决插件依赖问题
 * <p>
 * 定义依赖的插件，必须包含Setter方法
 * <p>
 * 比如: 定义@Dependency GameFrame gameApp
 * <p>
 * 就必须有public void setGameApp(GameFrame gameApp)方法
 * @author 石海兵 Create on 2013-9-18 下午5:15:13
 * @version 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dependency {

}
