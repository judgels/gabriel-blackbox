package org.iatoki.judgels.gabriel.blackbox.engines;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.iatoki.judgels.gabriel.GradingException;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResultDetails;
import org.iatoki.judgels.gabriel.blackbox.SubtaskFinalResult;
import org.iatoki.judgels.gabriel.blackbox.TestCase;
import org.iatoki.judgels.gabriel.blackbox.TestGroup;
import org.iatoki.judgels.gabriel.blackbox.configs.FunctionGradingConfig;
import org.iatoki.judgels.gabriel.blackbox.languages.PlainCppGradingLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public final class FunctionGradingEngineTest extends BlackBoxGradingEngineTest {

    private final FunctionGradingEngine engine;

    private final int timeLimit;
    private final int memoryLimit;
    private final List<TestGroup> testData;

    public FunctionGradingEngineTest() {
        super("function");

        this.timeLimit = 1000;
        this.memoryLimit = 65536;

        this.testData = ImmutableList.of(
                new TestGroup(0, ImmutableList.of(
                        new TestCase("sample_1.in", "sample_1.out", ImmutableSet.of(0)),
                        new TestCase("sample_2.in", "sample_2.out", ImmutableSet.of(0)),
                        new TestCase("sample_3.in", "sample_3.out", ImmutableSet.of(0))
                )),
                new TestGroup(-1, ImmutableList.of(
                        new TestCase("1_1.in", "1_1.out", ImmutableSet.of(-1)),
                        new TestCase("1_2.in", "1_2.out", ImmutableSet.of(-1)),
                        new TestCase("2_1.in", "2_1.out", ImmutableSet.of(-1)),
                        new TestCase("2_2.in", "2_2.out", ImmutableSet.of(-1)),
                        new TestCase("2_3.in", "2_3.out", ImmutableSet.of(-1))
                ))
        );

        this.engine = new FunctionGradingEngine();
        this.engine.setScorerLanguage(new PlainCppGradingLanguage());
    }

    @Test
    public void testAC() {
        addSourceFile("source", "aplusb-AC-lib.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithMainSourceFileAndCustomScorer("aplusb-AC.cpp", null));
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_AC, 100))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testACButWAAtSampleThatIsNotIncluded() {
        addSourceFile("source", "aplusb-WA-at-sample_3-lib.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithMainSourceFileAndCustomScorer("aplusb-WA-at-sample_3.cpp", null));
            assertEquals(result.getVerdict(), VERDICT_AC);
            assertEquals(result.getScore(), 100);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_AC, 100))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testWA80() {
        addSourceFile("source", "aplusb-WA-at-1_1-lib.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithMainSourceFileAndCustomScorer("aplusb-WA-at-1_1.cpp", null));
            assertEquals(result.getVerdict(), VERDICT_WA);
            assertEquals(result.getScore(), 80);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_WA, 80))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    @Test
    public void testOK90WithCustomScorer() {
        addSourceFile("source", "aplusb-WA-at-1_1-lib.cpp");

        try {
            GradingResult result = runEngine(engine, createConfigWithMainSourceFileAndCustomScorer("aplusb-WA-at-1_1.cpp", "scorer-nonbinary-OK10-at-1_1.cpp"));
            assertEquals(result.getVerdict(), VERDICT_OK);
            assertEquals(result.getScore(), 90);

            BlackBoxGradingResultDetails details = new Gson().fromJson(result.getDetails(), BlackBoxGradingResultDetails.class);
            assertEquals(details.getSubtaskResults(), ImmutableList.of(
                            new SubtaskFinalResult(-1, VERDICT_OK, 90))
            );
        } catch (GradingException e) {
            fail();
        }
    }

    private FunctionGradingConfig createConfigWithMainSourceFileAndCustomScorer(String mainSourceFile, String customScorer) {
        return new FunctionGradingConfig(timeLimit, memoryLimit, testData, mainSourceFile, customScorer);
    }
}
