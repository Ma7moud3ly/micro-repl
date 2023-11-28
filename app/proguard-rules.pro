# stop logs on release
-assumenosideeffects  class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

# For the code editor (TextMateLanguage)
-keep class org.eclipse.tm4e.**{*;}

