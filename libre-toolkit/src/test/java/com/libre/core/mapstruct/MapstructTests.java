// package com.libre.core.mapstruct;
//
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.ApplicationContext;
//
//
/// **
// * @author ZC
// * @date 2021/12/20 2:45
// */
// @SpringBootTest
// public class MapstructTests {
//
// @Autowired
// ApplicationContext applicationContext;
/// * @Test
// void copyTest() {
// SourceBean sourceBean = new SourceBean();
// sourceBean.setName("name1");
// sourceBean.setAge(18);
// BeanMapping convert = BeanMapping.CONVERT;
// TargetBean targetBean = convert.sourceToTarget(sourceBean);
//
// Assertions.assertNotNull(targetBean);
// Assertions.assertEquals(sourceBean.getName(), targetBean.getName());
// Assertions.assertEquals(sourceBean.getAge(), targetBean.getAge());
// }*/
//
/// * @Test
// void copy() {
// Stopwatch stopwatch = Stopwatch.createStarted();
// for (int i = 0; i < 10000000; i++) {
// SourceBean sourceBean = new SourceBean();
// sourceBean.setName("name1");
// sourceBean.setAge(18);
// BeanMapping convert = BeanMapping.INSTANCE;
// TargetBean targetBean = convert.sourceToTarget(sourceBean);
// }
// System.out.println("代码执行时长：" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
// stopwatch.reset();
// stopwatch.start();
// for (int i = 0; i < 10000000; i++) {
// SourceBean sourceBean = new SourceBean();
// sourceBean.setName("name");
// sourceBean.setAge(15);
// TargetBean targetBean = BeanUtils.copy(sourceBean, TargetBean.class);
// }
// System.out.println("代码执行时长：" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
// }*/
//
// @Test
// void test() {
//
// }
//
//
//
// }
