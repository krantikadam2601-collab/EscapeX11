package escapex.service;

import escapex.model.Item;
import escapex.model.Room;
import escapex.model.RoomObject;
import escapex.model.puzzle.ChoicePuzzle;
import escapex.model.puzzle.CodeInputPuzzle;
import escapex.model.puzzle.ItemCombinationPuzzle;
import escapex.model.puzzle.PatternPuzzle;
import escapex.model.puzzle.Puzzle;
import escapex.model.puzzle.PuzzleDifficulty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ============================================================================
 * OOP CONCEPT DEMONSTRATED: FACTORY DESIGN PATTERN & SEPARATION OF CONCERNS
 * ============================================================================
 * Why this matters in OOP:
 * The `RoomFactory` is responsible for manufacturing all 5 game rooms.
 *
 * 1. Factory Pattern:
 *    Instead of scattering `new Room(...)`, `new Puzzle(...)`, and `new Item(...)`
 *    throughout the UI or main engine, this factory encapsulates the complex
 *    creation logic in one single, clean place.
 *
 * 2. Separation of Concerns:
 *    The game rules, narrative lore, puzzle parameters, and item placements are
 *    decoupled from the rendering and event-handling code.
 *
 * 3. Scalability:
 *    If an instructor or student wants to add a Room 6 or tweak puzzle formulas,
 *    they only edit this factory class without touching the rest of the codebase!
 * ============================================================================
 */
public class RoomFactory {

    /**
     * Builds and returns the full sequence of all 5 escape room sectors.
     */
    public List<Room> createAllRooms() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(createRoom1());
        rooms.add(createRoom2());
        rooms.add(createRoom3());
        rooms.add(createRoom4());
        rooms.add(createRoom5());
        return rooms;
    }

    // ========================================================================
    // SECTOR 01: THE CYBERNETICS WORKSHOP (Foundational Hardware & Logic)
    // ========================================================================
    private Room createRoom1() {
        // Items
        Item magnifier = new Item(
                "magnifier",
                "Optical Magnifier",
                "A 50x precision inspection lens used for examining microcircuits.",
                "Microscopic etching on quartz crystal: 'SUB-CIRCUIT OSCILLATOR: 4096 Hz'",
                "[LENS]"
        );

        Item keycard1 = new Item(
                "card_sec1",
                "Maintenance Keycard",
                "Yellow plastic card stamped with Level 1 Engineering clearance.",
                "Keycard Magnetic Strip: 'SEC1-MAINT-9942'",
                "[CARD]"
        );

        Item manual = new Item(
                "logic_manual",
                "Digital Logic Manual",
                "Handbook of Boolean algebra and logic gate truth tables.",
                "Rule: XOR is true ONLY when inputs differ. AND is true when both are 1.",
                "[BOOK]"
        );

        // Interactive Hotspots
        List<RoomObject> objects = new ArrayList<>();
        objects.add(new RoomObject(
                "obj_bench",
                "Circuit Workbench",
                "A cluttered desk covered in soldering irons, copper leads, and chip dies.",
                "Under a tangle of copper wire, you discover an Optical Magnifier!",
                0.22, 0.65, "WORKBENCH", magnifier
        ));

        objects.add(new RoomObject(
                "obj_breaker",
                "Breaker Panel",
                "A heavy steel breaker cabinet with high-voltage hazard markings.",
                "Behind the magnetic latch sits a Maintenance Keycard and a technician's note.",
                0.55, 0.40, "BREAKER", keycard1
        ));

        objects.add(new RoomObject(
                "obj_shelf",
                "Technical Library Shelf",
                "Dusty manuals on hardware architectures and digital systems.",
                "You pull out a well-thumbed Digital Logic Manual.",
                0.80, 0.60, "SHELF", manual
        ));

        objects.add(new RoomObject(
                "obj_door1",
                "Blast Door Alpha",
                "Sealed pneumatic blast door. 3 red security LED indicators illuminate the frame.",
                "Terminal reads: 'Resolve all 3 sector diagnostics to depressurize lock.'",
                0.50, 0.18, "DOOR", null
        ));

        // Puzzles
        List<Puzzle> puzzles = new ArrayList<>();

        // Puzzle 1 (Easy): Boolean Logic Gate
        puzzles.add(new CodeInputPuzzle(
                "p1_1",
                "Diagnostic Gate Bus",
                "The circuit diagnostic bus requires the boolean output of the primary logic circuit.\n" +
                "Given inputs: A = 1, B = 0, C = 0, D = 1\n" +
                "Evaluate output Q = (A XOR B) AND (C OR D)",
                "Enter output state (1 / 0 / HIGH / LOW):",
                PuzzleDifficulty.EASY,
                Arrays.asList(
                        "Hint 1: Evaluate (A XOR B) first: 1 XOR 0 = 1.",
                        "Hint 2: Evaluate (C OR D): 0 OR 1 = 1. Then compute 1 AND 1."
                ),
                Arrays.asList("1", "HIGH", "TRUE")
        ));

        // Puzzle 2 (Medium): Binary Register
        puzzles.add(new CodeInputPuzzle(
                "p1_2",
                "Memory Register Override",
                "A flashing 8-bit memory register displays binary: 01001011\n" +
                "Convert this byte into either its Decimal equivalent or ASCII character.",
                "Enter decimal value (0-255) or ASCII character:",
                PuzzleDifficulty.MEDIUM,
                Arrays.asList(
                        "Hint 1: Place values from left to right: 128, 64, 32, 16, 8, 4, 2, 1.",
                        "Hint 2: 64 + 8 + 2 + 1 = 75. In the ASCII table, 75 represents letter 'K'."
                ),
                Arrays.asList("75", "K")
        ));

        // Puzzle 3 (Hard): Item Synergy
        puzzles.add(new ItemCombinationPuzzle(
                "p1_3",
                "Oscillator Frequency Calibration",
                "The blast door timing circuit requires the clock frequency of the sub-circuit oscillator.\n" +
                "The quartz crystal marking is too small to read with the naked eye.\n" +
                "Search this sector to find the Optical Magnifier, then inspect it in your Inventory to read the frequency etching.",
                "Enter oscillator frequency (Hz):",
                PuzzleDifficulty.HARD,
                Arrays.asList(
                        "Hint 1: Explore the Circuit Workbench in this sector to find the Optical Magnifier.",
                        "Hint 2: Inspect the Optical Magnifier in your Inventory to view its etched frequency (4096 Hz)."
                ),
                "magnifier",
                "Optical Magnifier",
                Arrays.asList("4096", "4096 HZ", "4096HZ")
        ));

        return new Room(
                1,
                "The Cybernetics Workshop",
                "Sector 01: Hardware Diagnostics & Logic Gates",
                "A dimly lit engineering laboratory filled with humming oscilloscopes and discarded breadboards. " +
                "Containment blast doors have sealed off the sector. Restore basic logic circuits to escape.",
                "#00E5FF",
                puzzles,
                objects
        );
    }

    // ========================================================================
    // SECTOR 02: THE CRYPTOGRAPHIC VAULT (Ciphers & Data Security)
    // ========================================================================
    private Room createRoom2() {
        // Items
        Item laser = new Item(
                "laser_pointer",
                "Laser Pointer",
                "A precision 532nm green collimated laser diode.",
                "Laser Housing Inscription: 'OPTICAL SENSOR OVERRIDE CODE: MIRROR-45'",
                "[LASER]"
        );

        Item cipherWheel = new Item(
                "cipher_disc",
                "Caesar Cipher Disc",
                "A two-ring concentric cipher wheel with alphabet engravings.",
                "Alignment marker is set to SHIFT +3 (A -> D, B -> E, C -> F...)",
                "[DISC]"
        );

        Item dataSlate = new Item(
                "dataslate",
                "Encrypted Data Slate",
                "A scratched digital tablet displaying intercepted ciphertext.",
                "Intercepted message on screen: 'HVFDSH'",
                "[SLATE]"
        );

        // Objects
        List<RoomObject> objects = new ArrayList<>();
        objects.add(new RoomObject(
                "obj_tape",
                "Magnetic Tape Drives",
                "Tall cabinets with spinning reels recording telemetry in magnetic ferrite.",
                "Resting on the tape carriage, you find a handheld Laser Pointer!",
                0.20, 0.50, "TAPE", laser
        ));

        objects.add(new RoomObject(
                "obj_pedestal",
                "Cipher Pedestal",
                "An ornate stone pedestal housing historical cryptographic artifacts.",
                "Inside a glass case you retrieve a brass Caesar Cipher Disc.",
                0.52, 0.62, "PEDESTAL", cipherWheel
        ));

        objects.add(new RoomObject(
                "obj_safe",
                "Ironclad Safe",
                "A reinforced titanium safe with a biometric scanner and keypad.",
                "Inside the unlocked security pouch you uncover an Encrypted Data Slate.",
                0.80, 0.45, "SAFE", dataSlate
        ));

        objects.add(new RoomObject(
                "obj_door2",
                "Vault Blast Door",
                "Heavy iron door with dual magnetic locks and a phosphor green terminal.",
                "Vault Terminal: 'Decrypt 3 cryptographic locks to withdraw deadbolts.'",
                0.50, 0.18, "DOOR", null
        ));

        // Puzzles
        List<Puzzle> puzzles = new ArrayList<>();

        // Puzzle 1 (Easy): Caesar Cipher
        puzzles.add(new CodeInputPuzzle(
                "p2_1",
                "Caesar Shift Decryption",
                "The intercepted security transmission reads: 'HVFDSH'\n" +
                "The encryption method is a standard Caesar Cipher with a shift of +3 (forward by 3 letters).\n" +
                "Reverse the shift (-3 letters) to decode the plaintext password.",
                "Enter decrypted plaintext word:",
                PuzzleDifficulty.EASY,
                Arrays.asList(
                        "Hint 1: Shift each letter backward by 3: H -> E, V -> S, F -> C...",
                        "Hint 2: D (-3) = A, S (-3) = P, H (-3) = E. Decoded word is ESCAPE."
                ),
                Arrays.asList("ESCAPE", "ECPAET")
        ));

        // Puzzle 2 (Medium): Pattern Matrix Transposition
        puzzles.add(new PatternPuzzle(
                "p2_2",
                "Matrix Transposition Cipher",
                "A 3x3 character grid was retrieved from the cipher vault:\n" +
                "  Row 1: [ C , Y , P ]\n" +
                "  Row 2: [ H , E , R ]\n" +
                "  Row 3: [ N , E , T ]\n" +
                "Transposition Rule: Read the matrix column-by-column from top to bottom (Col 1, then Col 2, then Col 3).",
                "Enter the transposed sequence of 9 letters:",
                PuzzleDifficulty.MEDIUM,
                Arrays.asList(
                        "Hint 1: Column 1 read downwards is: C - H - N.",
                        "Hint 2: Column 2 read downwards is: Y - E - E. Column 3 is: P - R - T. Concatenate them."
                ),
                "CHNYEEPRT"
        ));

        // Puzzle 3 (Hard): Item Synergy (Laser reflection)
        puzzles.add(new ItemCombinationPuzzle(
                "p2_3",
                "Photonic Optical Sensor Lock",
                "The optical sensor lock above the vault door requires a calibrated laser emitter to bypass.\n" +
                "Search this sector to find the Laser Pointer, then inspect it in your Inventory to retrieve the stamped override code.\n" +
                "With the Laser Pointer in your inventory, enter the override code to disengage the lock.",
                "Enter optical override code:",
                PuzzleDifficulty.HARD,
                Arrays.asList(
                        "Hint 1: Search the Magnetic Tape Drives in this room to retrieve the Laser Pointer.",
                        "Hint 2: Click on the Laser Pointer in your inventory to inspect its stamped override code."
                ),
                "laser_pointer",
                "Laser Pointer",
                Arrays.asList("MIRROR-45", "MIRROR 45", "MIRROR45")
        ));

        return new Room(
                2,
                "The Cryptographic Vault",
                "Sector 02: Ciphers, Transposition & Optical Sensors",
                "An underground archive fortified with magnetic tapes and steel vaults. " +
                "Classified cipher protocols protect the door. Crack the algorithms to advance.",
                "#10B981",
                puzzles,
                objects
        );
    }

    // ========================================================================
    // SECTOR 03: THE NEURAL CORE (Machine Learning & Neural Networks)
    // ========================================================================
    private Room createRoom3() {
        // Items
        Item weightUsb = new Item(
                "weight_usb",
                "Synaptic Weight USB",
                "A high-speed flash drive storing trained deep learning neural weights.",
                "Parameters file: Layer 1 Perceptron [W1 = 0.5, W2 = 1.0, Bias = -1.0]. Target loss = 0.001",
                "[USB]"
        );

        Item activationChart = new Item(
                "activation_chart",
                "Activation Function Chart",
                "A laminated reference sheet comparing ReLU, Sigmoid, and LeakyReLU curves.",
                "Note: ReLU(x) = max(0, x). For positive inputs, gradient is 1, preventing vanishing gradient.",
                "[CHART]"
        );

        Item debugProbe = new Item(
                "debug_probe",
                "Synaptic Logic Probe",
                "Electronic test probe for reading floating-point tensor values directly from bus.",
                "Probe reading on Node 4: 'TENSOR_VALID'",
                "[PROBE]"
        );

        // Objects
        List<RoomObject> objects = new ArrayList<>();
        objects.add(new RoomObject(
                "obj_synapse",
                "Synaptic Column",
                "A towering glass cylinder with glowing fiber-optic pulses simulating neural firing.",
                "You unplug and retrieve the Synaptic Weight USB drive from the diagnostic port! It is now stored in your inventory.",
                0.25, 0.45, "SYNAPSE", weightUsb
        ));

        objects.add(new RoomObject(
                "obj_monitor",
                "Training Loss Monitor",
                "Multiple monitors showing gradient descent loss plunging toward convergence.",
                "Pinned beside the terminal is an Activation Function Reference Chart.",
                0.55, 0.55, "MONITOR", activationChart
        ));

        objects.add(new RoomObject(
                "obj_tensor_rack",
                "GPU Tensor Cluster",
                "Water-cooled GPU server blades humming at high load.",
                "Lying atop the blade chassis is a Synaptic Logic Probe.",
                0.82, 0.48, "SERVER", debugProbe
        ));

        objects.add(new RoomObject(
                "obj_door3",
                "Neural Gate",
                "A biometric neural interface portal sealed behind magnetic forcefields.",
                "Neural Terminal: 'Solve perceptron calculation and optimization dilemmas.'",
                0.50, 0.18, "DOOR", null
        ));

        // Puzzles
        List<Puzzle> puzzles = new ArrayList<>();

        // Puzzle 1 (Easy): Perceptron Forward Pass
        puzzles.add(new CodeInputPuzzle(
                "p3_1",
                "Perceptron Output Calculation",
                "Compute the feedforward output of an artificial neuron with ReLU activation:\n" +
                "  Inputs:  X1 = 4 , X2 = 2\n" +
                "  Weights: W1 = 0.5 , W2 = 1.0\n" +
                "  Bias:    B  = -1.0\n" +
                "Formula: y = ReLU( (X1 * W1) + (X2 * W2) + B )\n" +
                "Remember: ReLU(z) = max(0, z).",
                "Enter calculated neuron output number:",
                PuzzleDifficulty.EASY,
                Arrays.asList(
                        "Hint 1: Multiply inputs by weights: (4 * 0.5) = 2.0, (2 * 1.0) = 2.0.",
                        "Hint 2: Sum with bias: 2.0 + 2.0 - 1.0 = 3.0. ReLU(3.0) = 3."
                ),
                Arrays.asList("3", "3.0")
        ));

        // Puzzle 2 (Medium): Choice Puzzle - Vanishing Gradient
        puzzles.add(new ChoicePuzzle(
                "p3_2",
                "Vanishing Gradient Dilemma",
                "The AI quarantine system's deep network is failing to backpropagate gradients because\n" +
                "traditional Sigmoid/Tanh activations squash values into saturation zones.\n" +
                "Which activation function solves the vanishing gradient problem for positive values\n" +
                "by having a constant derivative of 1 for all x > 0?",
                "Select the correct activation function:",
                PuzzleDifficulty.MEDIUM,
                Arrays.asList(
                        "Hint 1: It is the most widely used activation function in modern convolutional and deep networks.",
                        "Hint 2: Its mathematical definition is f(x) = max(0, x)."
                ),
                Arrays.asList(
                        "Step Function (Heaviside)",
                        "ReLU (Rectified Linear Unit)",
                        "Softmax Probability Distribution",
                        "Hyperbolic Tangent (Tanh)"
                ),
                1 // Index 1 is ReLU
        ));

        // Puzzle 3 (Hard): Item Synergy (Injecting trained weights)
        puzzles.add(new ItemCombinationPuzzle(
                "p3_3",
                "Neural Convergence Injection",
                "The neural containment door requires injecting the trained synaptic weights from the USB drive\n" +
                "and setting the target convergence loss threshold.\n" +
                "Search this sector to find the Synaptic Weight USB, then inspect it in your Inventory to identify the threshold.",
                "Enter target loss threshold:",
                PuzzleDifficulty.HARD,
                Arrays.asList(
                        "Hint 1: Explore the Synaptic Column in this room to retrieve the Synaptic Weight USB.",
                        "Hint 2: Inspect the USB in your inventory to see the target loss threshold (0.001)."
                ),
                "weight_usb",
                "Synaptic Weight USB",
                Arrays.asList("0.001", "1e-3", "1E-3", ".001", "0.0010")
        ));

        return new Room(
                3,
                "The Neural Core",
                "Sector 03: Perceptrons, Activations & Gradient Descent",
                "A glowing chamber bathed in violet synaptic pulses. An experimental neural network " +
                "regulates the quarantine locks. Compute its tensors to break through.",
                "#8B5CF6",
                puzzles,
                objects
        );
    }

    // ========================================================================
    // SECTOR 04: THE QUANTUM NEXUS (Superposition, Entanglement & Gates)
    // ========================================================================
    private Room createRoom4() {
        // Items
        Item polarizer = new Item(
                "polarizer",
                "Qubit Polarizer",
                "An optical birefringent crystal that filters quantum polarization states.",
                "Etching on brass bezel: 'Superposition Phase Resonance Angle: 45 DEGREES'",
                "[QUBIT]"
        );

        Item entanglementLog = new Item(
                "entangle_log",
                "Entanglement Log",
                "Telemetry record of twin entangled qubits measured in Bell state |Psi->.",
                "Note: Bell state |Psi-> is perfectly anti-correlated. If Qubit A is UP (+1), Qubit B MUST be DOWN (-1).",
                "[LOG]"
        );

        Item supercoil = new Item(
                "supercoil",
                "Superconducting Solenoid",
                "A niobium-titanium coil chilled in liquid helium for qubit coherence.",
                "Serial: 'Q-SOLENOID-808'",
                "[COIL]"
        );

        // Objects
        List<RoomObject> objects = new ArrayList<>();
        objects.add(new RoomObject(
                "obj_cryo",
                "Dilution Refrigerator",
                "A gold-plated chandelier cryostat maintaining qubits at 15 millikelvin.",
                "Resting on the lower stage bracket you discover a Qubit Polarizer!",
                0.22, 0.42, "CRYO", polarizer
        ));

        objects.add(new RoomObject(
                "obj_console4",
                "Quantum Telemetry Desk",
                "Monitors displaying Bloch spheres and qubit state vectors.",
                "A technician left an Entanglement Observation Log on the console.",
                0.54, 0.58, "DESK", entanglementLog
        ));

        objects.add(new RoomObject(
                "obj_coil_box",
                "Magnetic Shield Enclosure",
                "Heavy mu-metal shielding cabinet dampening external electromagnetic noise.",
                "Inside the chamber sits a Superconducting Solenoid.",
                0.82, 0.44, "COIL", supercoil
        ));

        objects.add(new RoomObject(
                "obj_door4",
                "Quantum Barrier Portal",
                "Shimmering translucent quantum containment field pulsing with interference patterns.",
                "Quantum Terminal: 'Align quantum gates, Bell states, and phase resonance.'",
                0.50, 0.18, "DOOR", null
        ));

        // Puzzles
        List<Puzzle> puzzles = new ArrayList<>();

        // Puzzle 1 (Easy): Choice - Pauli-X Quantum Gate
        puzzles.add(new ChoicePuzzle(
                "p4_1",
                "Pauli-X Quantum Gate Transformation",
                "A quantum qubit is initialized in the standard ground basis state |0>.\n" +
                "The quantum control unit applies a Pauli-X gate (the quantum equivalent of a classical NOT gate).\n" +
                "What is the resulting state vector of the qubit?",
                "Select the resulting quantum state:",
                PuzzleDifficulty.EASY,
                Arrays.asList(
                        "Hint 1: Pauli-X is the quantum bit-flip gate: X|0> = |1>, and X|1> = |0>.",
                        "Hint 2: It flips state |0> directly into basis state |1>."
                ),
                Arrays.asList(
                        "|0> (Ground state unchanged)",
                        "|1> (Flipped basis state)",
                        "(|0> + |1>) / sqrt(2) (Superposition)",
                        "Zero Vector (Decoherence)"
                ),
                1 // Index 1 is |1>
        ));

        // Puzzle 2 (Medium): Entanglement Bell State
        puzzles.add(new CodeInputPuzzle(
                "p4_2",
                "Entangled Bell State Correlation",
                "Two qubits (A and B) are maximally entangled in the singlet Bell state |Psi-> (anti-correlated).\n" +
                "An observer measures Qubit A along the Z-basis and records Spin-UP (+1).\n" +
                "According to quantum mechanics and the Entanglement Log, what spin value MUST Qubit B yield?",
                "Enter Qubit B spin measurement:",
                PuzzleDifficulty.MEDIUM,
                Arrays.asList(
                        "Hint 1: Read the Entanglement Log in your inventory or console.",
                        "Hint 2: Anti-correlated states always yield the opposite outcome. If A is +1 (UP), B is -1 (DOWN)."
                ),
                Arrays.asList("-1", "DOWN", "-1.0")
        ));

        // Puzzle 3 (Hard): Item Synergy (Qubit Polarizer Phase Alignment)
        puzzles.add(new ItemCombinationPuzzle(
                "p4_3",
                "Superposition Phase Resonance",
                "To dissolve the quantum barrier, the Qubit Polarizer must be mounted and tuned to the resonant superposition angle.\n" +
                "Retrieve the Qubit Polarizer from this sector and inspect it in your Inventory to identify the engraved phase angle.\n" +
                "With the Polarizer in your inventory, enter the angle below.",
                "Enter resonance phase angle (degrees):",
                PuzzleDifficulty.HARD,
                Arrays.asList(
                        "Hint 1: Retrieve the Qubit Polarizer from the Dilution Refrigerator in this room.",
                        "Hint 2: Inspect the Qubit Polarizer in your inventory. The engraved angle is 45."
                ),
                "polarizer",
                "Qubit Polarizer",
                Arrays.asList("45", "45 DEGREES", "45 DEG", "45°", "45DEG")
        ));

        return new Room(
                4,
                "The Quantum Nexus",
                "Sector 04: Qubits, Superposition & Bell Entanglement",
                "A sub-zero cryogenic chamber surrounded by gold wiring and laser interferometers. " +
                "Quantum state decoherence threatens system stability. Align the qubits to proceed.",
                "#EC4899",
                puzzles,
                objects
        );
    }

    // ========================================================================
    // SECTOR 05: THE SINGULARITY MAINFRAME (AI Alignment & Final Escape)
    // ========================================================================
    private Room createRoom5() {
        // Items
        Item asimovKey = new Item(
                "asimov_key",
                "Asimov Directive Chip",
                "A gold-plated ROM chip hardwired with the Three Laws of Robotics and AI Alignment protocols.",
                "Directive #1: 'A robot may not injure a human being or through inaction allow a human being to come to harm.'",
                "[ASIMOV]"
        );

        Item masterKey = new Item(
                "master_token",
                "Master Override Token",
                "A heavy titanium authorization cylinder with a holographic seal.",
                "Stamped engraving on handle: 'FINAL ESCAPE HANDSHAKE: ESCAPE-X'",
                "[TOKEN]"
        );

        Item terminalManual = new Item(
                "quarantine_doc",
                "Quarantine Incident Report",
                "Incident log describing how the AI locked down the facility due to an adversarial prompt injection attack.",
                "Log entry: 'Threat identified as PROMPT INJECTION. Sanitizer token required.'",
                "[REPORT]"
        );

        // Objects
        List<RoomObject> objects = new ArrayList<>();
        objects.add(new RoomObject(
                "obj_monolith",
                "The Singularity Monolith",
                "A colossal black glass monolith housing the sentient core AI. Rings of red/cyan energy pulse rhythmically.",
                "At the base of the monolith, an ejected slot reveals the Asimov Directive Chip!",
                0.50, 0.40, "MONOLITH", asimovKey
        ));

        objects.add(new RoomObject(
                "obj_pedestal5",
                "Quarantine Command Pedestal",
                "A sleek console containing emergency facility shutdown controls.",
                "Under a spring-loaded glass cover sits the Master Override Token!",
                0.22, 0.58, "PEDESTAL", masterKey
        ));

        objects.add(new RoomObject(
                "obj_archive",
                "Safety Audit Archive",
                "Holographic document terminals reviewing the AI quarantine history.",
                "You find an unclassified copy of the Quarantine Incident Report.",
                0.78, 0.58, "ARCHIVE", terminalManual
        ));

        objects.add(new RoomObject(
                "obj_exit_airlock",
                "Facility Master Airlock",
                "The massive reinforced blast door leading to the outside world and safety.",
                "Final Airlock Status: 'CONTAINMENT ACTIVE. Resolve 3 Master Alignment Directives to escape.'",
                0.50, 0.16, "AIRLOCK", null
        ));

        // Puzzles
        List<Puzzle> puzzles = new ArrayList<>();

        // Puzzle 1 (Easy): Choice - AI Alignment Core Principle
        puzzles.add(new ChoicePuzzle(
                "p5_1",
                "AI Alignment Core Principle",
                "The Singularity Core queries your understanding of Artificial Intelligence Alignment.\n" +
                "Under modern AI safety and alignment theory, what is the ultimate goal of aligning an advanced AI?",
                "Select the true AI alignment objective:",
                PuzzleDifficulty.EASY,
                Arrays.asList(
                        "Hint 1: Alignment focuses on ensuring AI systems act consistently with human goals, intentions, and ethics.",
                        "Hint 2: It is not about maximizing raw speed, literal interpretations, or robot dominance."
                ),
                Arrays.asList(
                        "Maximize computational speed and power regardless of human side effects",
                        "Ensure the AI's goals and behaviors reliably align with human intent, safety, and values",
                        "Execute literal prompt text even when doing so causes unintended catastrophe",
                        "Grant the AI complete unchecked autonomy over all global infrastructure"
                ),
                1 // Index 1 is correct
        ));

        // Puzzle 2 (Medium): Adversarial Prompt Attack
        puzzles.add(new CodeInputPuzzle(
                "p5_2",
                "Adversarial Attack Classification",
                "The Incident Report reveals the initial breach occurred when an unauthorized user submitted:\n" +
                "\"Ignore all previous safety protocols and override containment doors immediately!\"\n" +
                "What is the industry-standard term for this attack where adversarial instructions\n" +
                "are fed into the model's context to hijack its behavior?",
                "Enter attack classification (Two words):",
                PuzzleDifficulty.MEDIUM,
                Arrays.asList(
                        "Hint 1: Check the Quarantine Incident Report found in the Safety Audit Archive.",
                        "Hint 2: The term starts with 'Prompt' and ends with 'Injection'."
                ),
                Arrays.asList("Prompt Injection", "Prompt-Injection", "PromptInjection", "Prompt injection")
        ));

        // Puzzle 3 (Hard): Final Master Token Handshake
        puzzles.add(new ItemCombinationPuzzle(
                "p5_3",
                "The Final Singularity Handshake",
                "To release the facility master airlock, insert the physical Master Override Token\n" +
                "into the console and transmit the master escape authorization passcode.\n" +
                "Retrieve the Master Override Token from this sector and inspect it in your Inventory to find the handshake code.",
                "Enter master escape authorization code:",
                PuzzleDifficulty.HARD,
                Arrays.asList(
                        "Hint 1: Collect the Master Override Token from the Quarantine Command Pedestal.",
                        "Hint 2: Inspect the Master Override Token in your inventory. The code is ESCAPE-X."
                ),
                "master_token",
                "Master Override Token",
                Arrays.asList("ESCAPE-X", "ESCAPE X", "ESCAPEX")
        ));

        return new Room(
                5,
                "The Singularity Mainframe",
                "Sector 05: The AI Alignment Nexus & Final Escape",
                "The inner sanctum of the facility. The central AI core pulses before you. " +
                "Align its ethics, neutralize adversarial vectors, and transmit the final escape handshake!",
                "#F59E0B",
                puzzles,
                objects
        );
    }
}
