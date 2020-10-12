package com.progmatic.progmappbe.helpers.sourceevaluator;


import com.progmatic.progmappbe.helpers.sourceevaluator.compiler.CachedCompiler;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoruceCodeEvaluator {
    public static final String PACKAGE_OF_CLASSES_TO_TEST = "org.progmatic.sourcequiz.classtotest.";
    public static final String PACKAGE_OF_TEST_CLASSES = "org.progmatic.sourcequiz.test.";

    public EvaluationResult evaluateSourceCode(String codeToEvaluate, String unitTestCode)  {
        EvaluationResult ret = new EvaluationResult();
        try{
            MyClassloader mc1 = new MyClassloader();
            String className = getClassName(codeToEvaluate, false);
            CachedCompiler cachedCompiler = new CachedCompiler(null, null);
            Class classToTest = cachedCompiler.loadFromJava(mc1, className, codeToEvaluate);

            String unitTestClassname = getClassName(unitTestCode, true);
            Class testClass = cachedCompiler.loadFromJava(mc1, unitTestClassname, unitTestCode);
            ret.setCompilationSuccessfull(true);

            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            runOne(testClass, listener);
            TestExecutionSummary summary = listener.getSummary();
            //summary.printTo(new PrintWriter(System.out));
            ret.setUnitTestSuccessfull(isTestSuccesfull(summary));
            mc1 = null;
        }
        catch (Exception e){
            e.printStackTrace();
            ret.setCompilationSuccessfull(false);
            ret.getErrorMessages().add(e.getMessage());
        }

        return ret;
    }

    public void runOne(Class unitTestclass, SummaryGeneratingListener listener) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(DiscoverySelectors.selectClass(unitTestclass))
                .configurationParameter("junit.jupiter.execution.timeout.default", "2")
                .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }

    public boolean isTestSuccesfull(TestExecutionSummary summary){
        return  summary.getFailures().size() == 0
                && summary.getTestsAbortedCount() == 0
                && summary.getTestsSkippedCount() == 0
                && summary.getTestsStartedCount() == summary.getTestsSucceededCount();
    }

    public String getClassName(String codeToEvaluate, boolean isUnitTest) {
        Pattern pattern = Pattern.compile("public[ \\s]+class[ \\s]+");
        Matcher matcher = pattern.matcher(codeToEvaluate);
        StringBuilder sb = new StringBuilder("");
        if(matcher.find()){
            int index = matcher.end();
            char nextChar = codeToEvaluate.charAt(index);
            while(!Character.isSpaceChar(nextChar)){
                sb.append(nextChar);
                index++;
                nextChar = codeToEvaluate.charAt(index);
            }
        }
        if(sb.length()==0){
            return null;
        }
        if(isUnitTest){
            return PACKAGE_OF_TEST_CLASSES + sb.toString();
        }
        return PACKAGE_OF_CLASSES_TO_TEST + sb.toString();
    }
}
