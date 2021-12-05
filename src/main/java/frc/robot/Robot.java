// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;


import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private WPI_VictorSPX victor1 = new WPI_VictorSPX(4);
  private WPI_VictorSPX victor2 = new WPI_VictorSPX(2);
  private WPI_VictorSPX victor3 = new WPI_VictorSPX(3);
  private WPI_VictorSPX victor4 = new WPI_VictorSPX(6);

  private SpeedControllerGroup victor1_2 = new SpeedControllerGroup(victor1, victor2);
  private SpeedControllerGroup victor3_4 = new SpeedControllerGroup(victor3, victor4);

  private XboxController con = new XboxController(0);

  double[] speed ={0.3,0.4,0.5,0.6,0.6};
  int i = 0;

  boolean toggle = false;
  NetworkTableInstance inst;
  NetworkTable RPiTable;

  boolean inPosition = false;

  private DifferentialDrive drive = new DifferentialDrive(victor1_2, victor3_4); 

  private NetworkTableEntry entry_centerX;
  private NetworkTableEntry entry_centerY;
  private NetworkTableEntry entry_area;
  private NetworkTableEntry entry_x;
  private NetworkTableEntry entry_y;


  double centerX;
  double centerY;
  double area;
  double obj_x , obj_y;

  double r;
  double theta;
  double v=0.15;

  double vl,vr;
  

  private AHRS ahrs = new AHRS(SPI.Port.kMXP);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    ahrs.reset();

    inst = NetworkTableInstance.getDefault();
    RPiTable = inst.getTable("Vision");
    entry_centerX = RPiTable.getEntry("center_x");
    entry_centerY = RPiTable.getEntry("center_y");
    entry_area = RPiTable.getEntry("area");
    entry_x = RPiTable.getEntry("obj_x");
    entry_y = RPiTable.getEntry("obj_y");

    
  }


  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }     

    centerX = (entry_centerX.getDouble(0)-400)/400;
    centerY = entry_centerY.getDouble(0);
    area = entry_area.getDouble(0);
    obj_x = entry_x.getDouble(0);
    obj_y = entry_y.getDouble(0);

    // System.out.println(area);
    // System.out.println(obj_x);
    // System.out.println(obj_y);

    theta = Math.atan2(obj_y, obj_x);

    // System.out.println(centerX);
    if(area>=1000){
      //調方向
        // double speed = centerX;
        // if (speed<0) speed = -speed;
        // speed = Math.pow(speed, 0.26)*0.35;
        // if(centerX >= 1.0/60.0){
        //   victor1_2.set(speed);
        //   victor3_4.set(-speed);
        // }
        // else if(centerX <= -1.0/60.0){
        //   victor3_4.set(speed);
        //   victor1_2.set(-speed);
        // }
        // else{
        //   victor1_2.set(0);
        //   victor3_4.set(0);
        // }

      //邊轉邊撿球
        if(Math.abs(theta)<=0.000001){
          r=1e64;
        }
        else{
           r=0.5*Math.abs(Math.sqrt(obj_x*obj_x + obj_y*obj_y))*1/(Math.sin(theta));
           drive.curvatureDrive(0.15,0.075,true);
           System.out.println("1/r");
           System.out.println(1/r);
        }
    }
    else{
      drive.curvatureDrive(0,0,true);
    }
    
    
  }
  

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    // double xSpeed = 0;
    // double zRotation = 0;
    // drive.curvatureDrive(xSpeed, zRotation,true);
    if(Math.abs(con.getY(Hand.kRight))>0.1){
      victor3_4.set(-con.getY(Hand.kRight)*-speed[i]);
    }
    else{
      victor3_4.set(0);
    }
    if(Math.abs(con.getY(Hand.kLeft))>0.1){
      victor1_2.set(-con.getY(Hand.kLeft)*speed[i]);
    }
    else{
      victor1_2.set(0);
    }

    if (con.getBumperPressed(Hand.kRight)){
      if (i<4)
        i++;
    }
    if (con.getBumperPressed(Hand.kLeft)){
      if(i>0)
        i--;
    }  

  }
  

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    ahrs.reset();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  //   var gyro = Rotation2d.fromDegrees(-ahrs.getAngle());
  // drive.arcadeDrive(Math.sin(Math.PI*2/9)*0.7,Math.cos(Math.PI*2/9)*0.7);

  // r=0.5*Math.abs(Math.sqrt(obj_x*obj_x + obj_y*obj_y))*1/(Math.cos(90-theta));
  // drive.curvatureDrive(v,1/r,true);
  
  // if(area>=50){
  //   double speed = centerX;
  //   if (speed<0) speed = -speed;
  //   speed = Math.pow(speed, 0.26)*0.35;
  //   if(centerX >= 1.0/60.0){
  //     victor1_2.set(speed);
  //     victor3_4.set(-speed);
  //   }
  //   else if(centerX <= -1.0/60.0){
  //     victor3_4.set(speed);
  //     victor1_2.set(-speed);
  //   }
  //   else{
  //     victor1_2.set(0);
  //     victor3_4.set(0);
  //   }


  // drive.curvatureDrive(con.getY(Hand.kLeft)*0.3, con.getY(Hand.kRight)*1.3, true);
    
     
    drive.tankDrive(vl,vr);
  }
}