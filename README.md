Jour
====

[![Maven](https://github.com/SingingBush/jour/actions/workflows/maven.yml/badge.svg)](https://github.com/SingingBush/jour/actions/workflows/maven.yml)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=jour&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jour)

Jour is designed to simplify the use of Javassist for processing multiple classes. In short Jour is simple Aspect Oriented Programming AOP framework on top of Javassist.

Forked from https://sourceforge.net/projects/jour/ and updated to support Java version 8 to 21.

# Maven Artifacts are now available via github:

```xml
<dependency>
    <groupId>net.sf.jour</groupId>
    <artifactId>jour</artifactId>
    <version>2.1.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.sf.jour</groupId>
    <artifactId>jour-instrument</artifactId>
    <version>2.1.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.sf.jour</groupId>
    <artifactId>jour-maven-plugin</artifactId>
    <version>2.1.1</version>
</dependency>
```

# Documentation

In general the way to use jour is to define a _jour.xml_ file with aspects which define how your code can be instrumented either using your own implementation of _net.sf.jour.instrumentor.Instrumentor_, or by using one of the included implementations:

 - net.sf.jour.instrumentor.MethodExecutionTimeInstrumentor
 - net.sf.jour.instrumentor.ExceptionCatcherInstrumentor
 - net.sf.jour.instrumentor.MakeEmptyMethodInstrumentor
 - net.sf.jour.instrumentor.ReplaceMethodInstrumentor

For example:

```xml
<jour>
    <aspect type="net.sf.jour.instrumentor.MakeEmptyMethodInstrumentor">
        <typedef>com.example.*</typedef>
        <pointcut expr="* debug*(..)"/>
    </aspect>
    
    <aspect type="net.sf.jour.instrumentor.ReplaceMethodInstrumentor">
        <typedef>com.example.*</typedef>
        <pointcut expr="java.lang.String replaceMe(..)"/>
        <property name="code">
            <value><![CDATA[
                {
                return "replacement implementation";
                }
            ]]></value>
        </property>
    </aspect>
</jour>
```

For more information about how to use jour-instrument please see: [http://jour.sourceforge.net/instrumentation.html](http://jour.sourceforge.net/instrumentation.html)
