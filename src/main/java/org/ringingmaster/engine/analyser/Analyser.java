package org.ringingmaster.engine.analyser;

import org.ringingmaster.engine.analyser.proof.Proof;
import org.ringingmaster.engine.analyser.proof.BuildProof;
import org.ringingmaster.engine.analyser.pipelinedata.BuildAnalysisPipelineData;
import org.ringingmaster.engine.analyser.rows.FalseRowAnalysis;
import org.ringingmaster.engine.compiler.compiledcomposition.CompiledComposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * TODO comments???
 *
 * @author Steve Lake
 */
public class Analyser implements Function<CompiledComposition, Proof> {

    private final Logger log = LoggerFactory.getLogger(Analyser.class);


    @Override
    public Proof apply(CompiledComposition input) {

        log.info("[{}] > analyse ", input.getComposition().getLoggingTag());

        Proof proof =
                new BuildAnalysisPipelineData()
                        .andThen(new FalseRowAnalysis())
                        .andThen(new BuildProof())
                        .apply(input);

        log.info("[{}] < analyse", input.getComposition().getLoggingTag());

        return proof;
    }
}
