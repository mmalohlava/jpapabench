# JPapaBench - High-Level Architecture

## Overview

JPapaBench is a Java implementation of the PapaBench benchmark, which is an Unmanned Aerial Vehicle (UAV) autopilot system originally written in C. The system simulates an autopilot for controlling an aircraft's flight, including navigation, stabilization, and fly-by-wire controls.

## System Purpose

PapaBench is designed as a real-time embedded benchmark that demonstrates:
- Real-time task scheduling and execution
- Hardware device interaction and sensor data processing
- Control system implementation (PIDs, navigation algorithms)
- Safety-critical system design patterns
- Multi-threaded/multi-task coordination

## Core Architecture

The system follows a modular architecture with three main subsystems:

```
┌─────────────────────────────────────────────────────────────────┐
│                         JPapaBench System                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────────────┐         ┌──────────────────────┐  │
│  │  Autopilot Module      │◄───SPI──►│  FBW Module          │  │
│  │  (MCU0 - Master)       │  Bus    │  (MCU1 - Slave)      │  │
│  ├────────────────────────┤         ├──────────────────────┤  │
│  │ • Estimator            │         │ • Servo Control      │  │
│  │ • Navigator            │         │ • Radio Receiver     │  │
│  │ • Flight Plan          │         │ • Failsafe Logic     │  │
│  │ • Control PIDs         │         │ • Link to Autopilot  │  │
│  │ • Link to FBW          │         │                      │  │
│  └────────────────────────┘         └──────────────────────┘  │
│           ▲                                    │               │
│           │                                    ▼               │
│     ┌─────┴──────┐                    ┌──────────────┐        │
│     │  Sensors   │                    │   Servos     │        │
│     ├────────────┤                    ├──────────────┤        │
│     │ • GPS      │                    │ • Elevator   │        │
│     │ • IR       │                    │ • Aileron    │        │
│     │ • Pressure │                    │ • Rudder     │        │
│     └────────────┘                    │ • Motors     │        │
│                                        └──────────────┘        │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Simulator Module (Optional)                 │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │ • Flight Model (Physics)                                 │  │
│  │ • GPS Simulator                                          │  │
│  │ • IR Simulator                                           │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1. **Autopilot Module** (MCU0)
The autopilot is responsible for high-level flight control and navigation:

**Key Components:**
- **Estimator**: Estimates the aircraft's position and attitude based on sensor data (GPS, IR sensors)
- **Navigator**: Manages flight plan execution and navigation logic
- **LinkToFBW**: Communication interface to the Fly-By-Wire module

**Devices:**
- GPS Device
- IR (Infrared) Device for attitude sensing
- Pressure, Compass, Air Speed sensors
- Video Camera
- Radio Transmitter/Modem

### 2. **Fly-By-Wire (FBW) Module** (MCU1)
The FBW module handles low-level servo control and actuator management:

**Key Components:**
- **LinkToAutopilot**: Communication interface to the Autopilot module
- **Servo Control**: Direct control of aircraft control surfaces

**Devices:**
- Radio Receiver (for manual control input)
- Servos (Elevator, Aileron, Rudder, Motors)
- Counters for timing

**Operating Modes:**
- Manual: Direct pilot control via radio
- Auto: Autopilot control
- Failsafe: Safety mode when communication is lost

### 3. **Simulator Module**
Provides a software-based flight environment for testing without hardware:

**Components:**
- **Flight Model**: Physics simulation of aircraft behavior
- **GPS Simulator**: Simulates GPS sensor readings
- **IR Simulator**: Simulates infrared sensor readings

## Task Architecture

The system uses periodic tasks with specific execution rates:

### Autopilot Tasks:
- **GPS Data Reception** (250ms) - Read GPS sensor data
- **Navigation Task** (250ms) - Execute flight plan and navigation logic  
- **Altitude Control** (250ms) - Maintain desired altitude
- **Climb Control** (250ms) - Control climb/descent rate
- **Stabilization** (50ms) - Maintain aircraft stability using PIDs
- **Link FBW Send** (50ms) - Send commands to FBW module
- **Radio Control** (25ms) - Process radio control inputs
- **Reporting Task** (100ms) - Status reporting and telemetry

### FBW Tasks:
- **Test PPM** (25ms) - Process PPM (Pulse Position Modulation) signals
- **Check Failsafe** (50ms) - Monitor for failsafe conditions
- **Servo Transmit** (50ms) - Send commands to servos
- **Send Data to Autopilot** (25ms) - Communicate status to autopilot

### Simulator Tasks:
- **Flight Model Update** - Physics simulation updates
- **GPS Simulation** - Generate GPS readings
- **IR Simulation** - Generate IR sensor readings

## Communication Architecture

### Inter-Module Communication (SPI Bus)
- **SPIBus**: Simulates Serial Peripheral Interface for communication between Autopilot (Master) and FBW (Slave)
- **InterMCUMsg**: Message format for inter-module communication
- Autopilot acts as SPI Master (MCU0)
- FBW acts as SPI Slave (MCU1)

### Data Flow:
1. Sensors → Autopilot (GPS, IR data)
2. Autopilot → Navigator (position, attitude)
3. Navigator → Flight Plan (waypoints, stages)
4. Autopilot → FBW (control commands via SPI)
5. FBW → Servos (actuation commands)
6. FBW → Autopilot (status feedback via SPI)

```
   ┌─────────┐
   │ Sensors │
   └────┬────┘
        │ (GPS, IR, Pressure)
        ▼
   ┌────────────┐       ┌──────────────┐
   │ Estimator  │──────►│  Navigator   │
   │ (Position, │       │ (Flight Plan │
   │  Attitude) │       │  Execution)  │
   └────────────┘       └──────┬───────┘
                               │
                               ▼
                        ┌──────────────┐
                        │  PIDs        │
                        │ (Control     │
                        │  Outputs)    │
                        └──────┬───────┘
                               │
                               ▼ (via SPI Bus)
                        ┌──────────────┐
                        │  FBW Module  │
                        │ (Servo       │
                        │  Commands)   │
                        └──────┬───────┘
                               │
                               ▼
                        ┌──────────────┐
                        │   Servos     │
                        │ (Actuation)  │
                        └──────────────┘
```

## Flight Plan Architecture

Flight plans define the aircraft's mission using:

**Waypoints**: 3D positions (x, y, altitude) defining flight path

**Navigation Blocks**: Groups of navigation stages
- Each block contains multiple stages
- Stages define specific flight behaviors (goto waypoint, circle, hold altitude)

**Navigation Stages**: Atomic flight behaviors
- Extend NavigatorCommands for access to navigation primitives
- Can transition between stages based on conditions
- Support commands like: gotoWaypoint(), circle(), setAltitude(), etc.

**Example Flight Plan Structure:**
```
FlightPlan
├── Waypoints (Position3D array)
├── Navigation Blocks
│   ├── Block 0: Takeoff
│   │   ├── Stage 0: Climb to secure altitude
│   │   └── Stage 1: Proceed to first waypoint
│   └── Block 1: Mission
│       ├── Stage 0: Navigate waypoint pattern
│       └── Stage 1: Return home
```

## Control Systems

### PID Controllers
The system uses multiple PID (Proportional-Integral-Derivative) controllers:

- **Altitude PID**: Maintains desired altitude
- **Climb PID**: Controls vertical speed
- **Course PID**: Controls heading/direction
- **Roll/Pitch PIDs**: Attitude stabilization

### Control Flow:
1. Estimator provides current state (position, attitude, speed)
2. Navigator computes desired state from flight plan
3. PIDs calculate control outputs (elevator, aileron, rudder, throttle)
4. Commands sent to FBW for servo actuation

## Platform Implementations

JPapaBench provides multiple implementations targeting different Java platforms:

### 1. **jpapabench-core**
Platform-independent core logic:
- Module interfaces and implementations
- Task logic and handlers
- PIDs and control algorithms
- Flight plan framework
- Device interfaces

### 2. **jpapabench-pj (Plain Java)**
Uses standard Java threading:
- Java Thread-based task execution
- Simple real-time scheduling using Thread priorities
- Standard Java synchronization

### 3. **jpapabench-rtsj (Real-Time Specification for Java)**
Uses RTSJ constructs:
- RealtimeThread for time-critical tasks
- Scoped and Immortal memory for deterministic allocation
- Priority-based real-time scheduling
- Asynchronous event handlers

### 4. **jpapabench-scj (Safety-Critical Java)**
Uses Safety-Critical Java Level 0:
- CyclicExecutive for deterministic scheduling
- PeriodicEventHandler for periodic tasks
- Mission-based execution model
- Restricted memory allocation (Mission/Immortal memory)
- Cyclic scheduling with minor cycles (25ms) and major cycle (250ms)

## Scheduling Models

### Plain Java / RTSJ:
- Priority-based preemptive scheduling
- Tasks run as independent threads
- Synchronization via monitors/locks

### SCJ Level 0:
- Time-triggered cyclic executive
- Fixed timeline schedule with frames
- Minor cycle: 25ms (highest frequency tasks)
- Major cycle: 250ms (complete schedule repeats)
- No preemption within handlers
- Deterministic execution order within each frame

**SCJ Schedule Example:**
```
Frame 0 (0ms):   RadioControl, SendDataToAutopilot, TestPPM
Frame 1 (25ms):  RadioControl, SendDataToAutopilot, TestPPM
Frame 2 (50ms):  Stabilization, CheckFailsafe, LinkFBWSend, RadioControl, ...
Frame 3 (75ms):  RadioControl, SendDataToAutopilot, TestPPM
...
Frame 10 (250ms): Navigation, AltitudeControl, ClimbControl, GPS, ...
```

## Module Dependencies and Initialization

### Initialization Flow:
1. **PapaBench** top-level system creation
2. Module creation and configuration:
   - AutopilotModule (with Estimator, Navigator)
   - FBWModule (with LinkToAutopilot)
   - SPIBusChannel (communication)
3. Device setup (GPS, IR, Servos, etc.)
4. Flight plan configuration
5. Module initialization (init() calls)
6. Task/handler registration
7. Execution start

### Dependency Injection Pattern:
The system uses setter-based dependency injection:
- Modules receive dependencies via setXXX() methods
- Devices and sub-modules injected before init()
- Allows flexibility in module composition
- Supports different platform-specific implementations

## Safety and Redundancy

### Failsafe Mechanisms:
- **Radio signal loss**: FBW enters failsafe mode
- **Communication timeout**: Between Autopilot and FBW
- **Sensor validation**: GPS and IR data validation
- **Control limits**: Bounded servo commands and control outputs

### Mode Management:
- **Manual Mode**: Direct pilot control
- **Auto Mode**: Autopilot control following flight plan
- **Home Mode**: Automatic return to home position
- **Failsafe Mode**: Safe default behavior when failures detected

## Build and Project Structure

### Module Organization:
```
jpapabench/
├── jpapabench-core/           # Platform-independent core
├── jpapabench-core-flightplans/  # Flight plan implementations
├── jpapabench-pj/             # Plain Java implementation
├── jpapabench-rtsj/           # RTSJ implementation
├── jpapabench-scj/            # Safety-Critical Java implementation
└── jpapabench-build/          # Build configuration
```

### Key Packages:
- `papabench.core.autopilot.*` - Autopilot logic
- `papabench.core.fbw.*` - Fly-By-Wire logic
- `papabench.core.commons.*` - Shared components
- `papabench.core.simulator.*` - Simulation environment
- `papabench.core.bus.*` - Inter-module communication

## Summary

JPapaBench demonstrates a well-architected real-time embedded system with:
- **Modular design**: Clear separation between Autopilot, FBW, and Simulator
- **Platform abstraction**: Core logic independent of execution platform
- **Real-time scheduling**: Support for multiple scheduling approaches
- **Safety-critical patterns**: Failsafe modes, bounded execution, deterministic behavior
- **Extensibility**: Flight plans can be easily created and modified
- **Testability**: Simulator allows testing without hardware

The architecture balances flexibility (multiple platform implementations) with determinism (SCJ with cyclic scheduling) while maintaining the essential characteristics of a real-time autopilot system.
