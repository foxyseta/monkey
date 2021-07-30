# Java commands
JD = javadoc
JC = javac
JR = java

# Directories
LIB_DIR = lib
SRC_DIR  = src
DOCS_DIR = docs
BUILD_DIR  = build

# Packages and classes
DOCS_PACKAGE = monkey
MAIN_CLASS = mnkgame.MNKGame
PLAYER_CLASS = monkey.MoNKey
PLAYER_TESTER_CLASS = mnkgame.MNKPlayerTester
TESTER_CLASS = monkey.Tester

# Command line options
OPTIONS = -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" -Xmx8G
OPTIONS_DEBUG = $(OPTIONS) -Xdebug \
								-Xrunjdwp:transport=dt_socket,address=5000,server=y,suspend=y 

# Source files
PLAYER_FILE = monkey/MoNKey.java
TESTER_FILE = monkey/Tester.java

# Default parameters (can also be specified from command line"
MNK = 3 3 3

# Plays a single game
run:
	@echo "Running..."
	@$(JR) $(OPTIONS) $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

# Plays a single game (debug mode)
run-debug:
	@echo "Running..."
	@$(JR) $(OPTIONS_DEBUG) $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

# Runs some preliminar checks
test:
	@echo "Testing..."
	@$(JR) $(OPTIONS) $(TESTER_CLASS)

# Runs some preliminar checks (debug mode)
test-debug:
	@echo "Testing..."
	@$(JR) $(OPTIONS_DEBUG) $(TESTER_CLASS)

# Rebuilds the whole project from zero
build: clean-build
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -cp "$(LIB_DIR)/*" -d "$(BUILD_DIR)/" \
	 -sourcepath "$(SRC_DIR)/" "$(SRC_DIR)/$(PLAYER_FILE)" \
	 "$(SRC_DIR)/$(TESTER_FILE)" -Werror

# Rebuilds the whole project from zero (debug mode)
build-debug: clean-build
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -g -cp "$(LIB_DIR)/*" -d "$(BUILD_DIR)/" -sourcepath "$(SRC_DIR)/" \
	 "$(SRC_DIR)/$(PLAYER_FILE)" "$(SRC_DIR)/$(PLAYER_FILE)" \
	 "$(SRC_DIR)/$(TESTER_FILE)" -Werror

# Rebuilds documentation from zero
docs: clean-docs
	@$(JD) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" -d "$(DOCS_DIR)/" -sourcepath \
	 "$(SRC_DIR)/" -subpackages $(DOCS_PACKAGE)

# Removes both binaries and documentation
clean: clean-build clean-docs

# Removes binaries
clean-build:
	@$(RM) -rf $(BUILD_DIR)

# Removes documentation
clean-docs:
	@$(RM) -rf $(DOCS_DIR) 
