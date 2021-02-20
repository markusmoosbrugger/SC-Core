package at.uibk.dps.sc.core.modules;

import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.config.annotations.Order;
import org.opt4j.core.config.annotations.Required;
import org.opt4j.core.start.Constant;
import at.uibk.dps.ee.guice.modules.EeModule;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUser;
import at.uibk.dps.sc.core.interpreter.ScheduleInterpreterUserSingle;
import at.uibk.dps.sc.core.scheduler.Scheduler;
import at.uibk.dps.sc.core.scheduler.SchedulerRandom;
import at.uibk.dps.sc.core.scheduler.SchedulerSingleOption;

/**
 * The {@link SchedulerModule} configures the binding of the scheduling-related
 * interfaces.
 * 
 * @author Fedor Smirnov
 *
 */
public class SchedulerModule extends EeModule {

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
    Random
  }

  @Order(1)
  @Info("The mode used to schedule user tasks.")
  public SchedulingMode schedulingMode = SchedulingMode.SingleOption;

  @Order(2)
  @Info("The number of mappings to pick for each user task.")
  @Constant(namespace = SchedulerRandom.class, value = "mappingsToPick")
  @Required(property = "schedulingMode", elements = "Random")
  public int mappingsToPick = 1;

  @Override
  protected void config() {
    bind(ScheduleInterpreterUser.class).to(ScheduleInterpreterUserSingle.class);
    if (schedulingMode.equals(SchedulingMode.SingleOption)) {
      bind(Scheduler.class).to(SchedulerSingleOption.class);
    } else if (schedulingMode.equals(SchedulingMode.Random)) {
      bind(Scheduler.class).to(SchedulerRandom.class);
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
}
