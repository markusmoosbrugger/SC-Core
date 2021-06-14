package at.uibk.dps.sc.core.decorators;

import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.core.enactable.FunctionDecoratorFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.opt4j.core.start.Constant;

/**
 * The {@link DecoratorEnactmentModelUpdateFactory} decorates functions by
 * wrapping them in {@link DecoratorEnactmentModelUpdate}s.
 *
 * @author Markus Moosbrugger
 */
@Singleton public class DecoratorEnactmentModelUpdateFactory extends FunctionDecoratorFactory {

  public final int priority;

  /**
   * The injection constructor.
   *
   * @param priority the priority of the decorator (see parent class comments)
   */
  @Inject
  public DecoratorEnactmentModelUpdateFactory(
      @Constant(value = "prio", namespace = DecoratorEnactmentModelUpdateFactory.class)
      final int priority) {
    this.priority = priority;
  }

  @Override
  public EnactmentFunction decorateFunction(final EnactmentFunction function) {
    return new DecoratorEnactmentModelUpdate(function);
  }

  @Override
  public int getPriority() {
    return priority;
  }
}
