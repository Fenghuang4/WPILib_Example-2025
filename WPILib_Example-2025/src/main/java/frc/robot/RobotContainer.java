// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.SubsystemConstants;
import frc.robot.Subsystems.Subsystem;

  /*
  * RobotContainer is where stuff happens.
  * All the subsystem instances are here.
  * All of the commands are scheduled here.
  * Anything related to control is here.
  */
public class RobotContainer {
  // controller for driver
  private final CommandXboxController driver;
  // subsystem
  private final Subsystem subsystem;

  public RobotContainer() {
    // objects are initialized in constructor
    driver = new CommandXboxController(Constants.driver);
    subsystem = new Subsystem(SubsystemConstants.kMotor1ID);
    configureBindings();
  }

  private void configureBindings() {
    // binding command to button
    driver.x().whileTrue(subsystem.bar());
  }

  // auto commands will go here, there can be multiple if a choser system is used
  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
