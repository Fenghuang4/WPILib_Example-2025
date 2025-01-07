package frc.robot.Subsystems;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.SubsystemConstants;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class Subsystem extends SubsystemBase{

     /*
     * A subsystem class coresponds to a group of hardware components that function together.
     * These can include motor controllers, solenoids and various sensors.
     * The use of CAN motor controllers requires 3rd party libraries.
     * Will likely be using Phoenix v6 and RevLib.
     * Use TalonFX for Falcon500, Kraken X60, Kraken X44.
     * Use SparkMAX for NEO, NEO550, NEO Vortex, brushed.
     * 
     * Commands corespond to actions done by the subsystem.
     * Subsystems have inherent state machines that permit only one command to run at a time.
     * Commands can be created with methods that return a command.
     * For a command that runs once, return a new InstantCommand();
     * For a command that runs continuously, return a new RunCommand();
     */

    // declare objects as private of course
    private final SparkMax motor1;
    private final SparkClosedLoopController controller1;
    private final TalonFX motor2;
    private final PositionVoltage m2Volt = new PositionVoltage(0).withSlot(0);
    private final DigitalInput limit;
    private final Solenoid solenoid;

    public Subsystem(int motor1ID) {
        // initialize objects in constructor
        // if there are multiple instances of a subsystem, pass the CAN ID/ PWM port / Pneumatic Port by parameter
        // For Rev motor controllers, specify motor type for brushed or brushless.
        motor1 = new SparkMax(motor1ID, MotorType.kBrushless);
        // PID setup https://docs.revrobotics.com/revlib
        // this PID will run directly on the SparkMAX
        SparkMaxConfig maxConfig = new SparkMaxConfig();
        maxConfig.closedLoop
            .p(SubsystemConstants.kP)
            .i(SubsystemConstants.kI)
            .d(SubsystemConstants.kD)
            .outputRange(SubsystemConstants.kMin, SubsystemConstants.kMax);
        maxConfig.idleMode(IdleMode.kBrake);
        motor1.configure(maxConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        controller1 = motor1.getClosedLoopController();

        // if a subsystem has only one instance, CAN IDs can be passed directly.
        // setup for TalonFX
        motor2 = new TalonFX(SubsystemConstants.kMotor2ID);
        TalonFXConfiguration talonConfig = new TalonFXConfiguration();
        talonConfig.Slot0.kP = SubsystemConstants.kP;
        talonConfig.Slot0.kI = SubsystemConstants.kI;
        talonConfig.Slot0.kD = SubsystemConstants.kD;
        talonConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        motor2.getConfigurator().apply(talonConfig);
        
        // simple declarations of genaric devices
        limit = new DigitalInput(SubsystemConstants.kLimitDIO);
        solenoid = new Solenoid(PneumaticsModuleType.REVPH, SubsystemConstants.kSolenoidPNU);

        // this command will run when no others are scheduled
        setDefaultCommand(foo());
    }

    private void motorsToPos() {
        if(!isLimit()) {
            controller1.setReference(0, ControlType.kPosition);
            motor2.setControl(m2Volt.withPosition(0));
        }
    }

    // returning state of digital input
    private boolean isLimit() {
        // digital inputs return true when false or disconnected by default
        return !limit.get();
    }

    public Command foo() {
        return run(/* functions are passed to commands as lambda */()-> motorsToPos());
    }

    public Command bar() {
        // this is a command composition created using decorators
        // there are other ways to combine commands, but this is the best at the moment
        return runOnce(()-> solenoid.set(true)).andThen(new WaitCommand(1)).andThen(runOnce(()-> solenoid.set(false)));
    }
}
