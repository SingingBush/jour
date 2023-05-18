/**
 * @author Samael Bate (singingbush)
 * created on 18/05/2023
 */
module jour.instrument {

    requires java.xml;

    requires org.slf4j;
    requires org.javassist;

    exports net.sf.jour.filter;
    exports net.sf.jour.instrumentor;
    exports net.sf.jour.processor;
    exports net.sf.jour.signature;
    exports net.sf.jour;
}
