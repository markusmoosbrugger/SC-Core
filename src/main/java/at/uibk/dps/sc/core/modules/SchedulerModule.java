package at.uibk.dps.sc.core.modules;

import at.uibk.dps.ee.guice.modules.EeModule;
import at.uibk.dps.ee.guice.modules.FunctionModule;
import at.uibk.dps.sc.core.decorators.DecoratorEnactmentModelUpdateFactory;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUserSingle;
import at.uibk.dps.sc.core.scheduler.*;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Order;
import org.opt4j.core.config.annotations.Required;
import org.opt4j.core.start.Constant;



/**
 * The {@link SchedulerModule} configures the binding of the scheduling-related
 * interfaces.
 *
 * @author Fedor Smirnov
 */
public class SchedulerModule extends FunctionModule {

  /**
   * Enum defining different scheduling modes.
   *
   * @author Fedor Smirnov
   */
  public enum SchedulingMode {
    /**
     * Expects a single scheduling option
     */
    SingleOption,
    /**
     * Random scheduling
     */
    Random,
    /**
     * Size threshold (should actually be a transmission option)
     */
    SizeConstraint,

    /**
     * Scheduling using Reinforcement Learning component from Pythia
     */
    RL,
  }


  @Order(1)
  @Info("The mode used to schedule user tasks.")
  public SchedulingMode schedulingMode = SchedulingMode.SingleOption;

  @Order(2)
  @Info("The number of mappings to pick for each user task.")
  @Constant(namespace = SchedulerRandom.class, value = "mappingsToPick")
  @Required(property = "schedulingMode", elements = {"Random", "SizeConstraint"})
  public int mappingsToPick = 1;

  @Order(3)
  @Info("Threshold in KB. Anything with an input with a larger size will be processed locally")
  @Constant(namespace = SchedulerDataSize.class, value = "sizeThreshold")
  @Required(property = "schedulingMode", elements = "SizeConstraint")
  public int sizeThresholdKb = 10;

  @Order(4)
  @Constant(value = "prio", namespace = DecoratorEnactmentModelUpdateFactory.class)
  @Required(property = "schedulingMode", elements = {"RL"})
  @Info("Decorators with lower prio are applied later.")
  public int enactmentModelUpdateDecoratorPriority = 20;

  @Override
  protected void config() {
    bind(ScheduleInterpreterUser.class).to(ScheduleInterpreterUserSingle.class);
    if (schedulingMode.equals(SchedulingMode.SingleOption)) {
      bind(Scheduler.class).to(SchedulerSingleOption.class);
    } else if (schedulingMode.equals(SchedulingMode.Random)) {
      bind(Scheduler.class).to(SchedulerRandom.class);
    } else if (schedulingMode.equals(SchedulingMode.SizeConstraint)) {
      bind(Scheduler.class).to(SchedulerDataSize.class);
    } else if (schedulingMode.equals(SchedulingMode.RL)) {
      bind(Scheduler.class).to(SchedulerRL.class);
      // add the function wrapper if the RL scheduler is used
      addFunctionDecoratorFactory(DecoratorEnactmentModelUpdateFactory.class);
    }


  }

  public SchedulingMode getSchedulingMode() {
    return schedulingMode;
  }

  public void setSchedulingMode(final SchedulingMode schedulingMode) {
    this.schedulingMode = schedulingMode;
  }

  public int getMappingsToPick() {
    return mappingsToPick;
  }

  public void setMappingsToPick(final int mappingsToPick) {
    this.mappingsToPick = mappingsToPick;
  }

  public int getSizeThresholdKb() {
    return sizeThresholdKb;
  }

  public void setSizeThresholdKb(final int sizeThresholdKb) {
    this.sizeThresholdKb = sizeThresholdKb;
  }

  public int getEnactmentModelUpdateDecoratorPriority() {
    return enactmentModelUpdateDecoratorPriority;
  }

  public void setEnactmentModelUpdateDecoratorPriority(int enactmentModelUpdateDecoratorPriority) {
    this.enactmentModelUpdateDecoratorPriority = enactmentModelUpdateDecoratorPriority;
  }

}
