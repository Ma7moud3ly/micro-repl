# stop logs on release
-assumenosideeffects  class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

# Please add these rules to your existing keep rules in order to suppress warnings.
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.FeatureDescriptor
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor

# For the code editor (TextMateLanguage)
-keep class org.eclipse.tm4e.**{*;}
-keep class org.joni.**{*;}