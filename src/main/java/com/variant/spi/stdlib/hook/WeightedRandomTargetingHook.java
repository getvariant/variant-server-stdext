package com.variant.spi.stdlib.hook;

import com.variant.server.spi.TargetingLifecycleEvent;
import com.variant.server.spi.TargetingLifecycleHook;
import com.variant.share.schema.State;
import com.variant.share.schema.Variation;
import com.variant.share.yaml.YamlMap;
import com.variant.share.yaml.YamlNode;
import com.variant.share.yaml.YamlScalar;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Targets randomly, honoring weights, which must be supplied in experience properties like so:
 * <pre>
 *    ...
 *    experiences:
 *      - name: foo
 *        properties:
 *          weight: 1
 *      - name: bar
 *          weight: 1.5
 *      - name: baz
 *          weight: 0.75
 *  * </pre>
 * By default, looks for the <code>weight</code> experience property, unless overridden in the
 * <code>init</code> key, like so (variation and state params are cas-sensitive):
 * <pre>
 *   hooks:
 *     - class: com.variant.spi.stdlib.lifecycle.WeightedRandomTargetingHook
 *       init:
 *         key: Weight
 * </pre>
 */
public class WeightedRandomTargetingHook implements TargetingLifecycleHook {

  final private String propName;

  public WeightedRandomTargetingHook() {
    propName = "weight";
  }
  public WeightedRandomTargetingHook(YamlNode<?> node) {
    var valOpt = Optional.ofNullable(((YamlMap) node).value().get("key"));
    propName =  valOpt
      .map(scalar -> ((YamlScalar<String>)scalar).value())
      .orElseThrow(
        () ->
          new RuntimeException(
            "Unable to value [%s] to a string literal"
              .formatted("foo"))
      );
  }
  private static Random rand = new Random();

  @Override
  public Optional<Variation.Experience> post(TargetingLifecycleEvent event) {

    Variation var = event.getVariation();
    State state = event.getState();
    List<Variation.Experience> definedExperiences = var.getExperiences().stream()
      .filter(e -> e.isDefinedOn(state))
      .toList();
    if (definedExperiences.isEmpty()) {
      throw new RuntimeException(
        String.format("No experiences in variation [%s] are defined on state [%s]", var.getName(), state.getName()));
    }
    double weightSum = definedExperiences.stream()
      .map(e -> Double.parseDouble(e.getParameters().get(propName)))
      .reduce(0D, Double::sum);

    double randVal = rand.nextDouble() * weightSum;
    weightSum = 0;
    for (Variation.Experience e: definedExperiences) {
      weightSum += Double.parseDouble(e.getParameters().get(propName));
      if (randVal < weightSum) {
        return Optional.of(e);
      }
    }
    // Should never get here.
    throw new RuntimeException("Unexpected state");
  }

}

