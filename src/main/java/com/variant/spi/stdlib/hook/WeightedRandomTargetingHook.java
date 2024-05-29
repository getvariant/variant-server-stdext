package com.variant.spi.stdlib.hook;

import com.variant.server.spi.TargetingLifecycleEvent;
import com.variant.server.spi.TargetingLifecycleHook;
import com.variant.share.error.VariantException;
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

  private String propName = "weight";

  public WeightedRandomTargetingHook(YamlNode<?> init) {
    Optional.ofNullable(init)
      .ifPresentOrElse (
        node -> {
          if (node instanceof YamlMap mapNode) {
            propName = Optional.ofNullable(mapNode.value().get("key"))
              .map(
                valueNode -> {
                  if (valueNode instanceof YamlScalar<?> scalarNode && scalarNode.value() instanceof String string) {
                    return string;
                  } else {
                    throw new VariantException(
                      "Unable to value [%s] to a string literal".formatted(valueNode));
                  }
                }
              )
              .orElseThrow(
                () -> new VariantException(
                  "Required key 'key' not found in the initializer map"));
          }
          else {
            throw new VariantException("The hook initializer must be a YAML map");
          }
        },
        // No init key was given
        () -> propName = "weight"
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
      .map(e -> {
        var val = e.getParameters().get(propName);
        return Double.parseDouble(e.getParameters().get(propName));
      })
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

