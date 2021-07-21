JD = javadoc
JC = javac
JR = java

LIB_DIR = lib
SRC_DIR  = src
DOCS_DIR = docs
BUILD_DIR  = build

DOCS_PACKAGE = monkey
MAIN_CLASS = mnkgame.MNKGame
PLAYER_CLASS = monkey.MoNKey
PLAYER_TESTER_CLASS = mnkgame.MNKPlayerTester
TESTER_CLASS = monkey.Tester

PLAYER_FILE = monkey/MoNKey.java
TESTER_FILE = monkey/Tester.java

MNK = 3 3 3
TEST = mnkgame.QuasiRandomPlayer -r 10 -v

run:
	@echo "Running..."
	@$(JR) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

run-debug:
	@echo "Running..."
	@$(JR) -Xdebug \
	 -Xrunjdwp:transport=dt_socket,address=5000,server=y,suspend=y \
	 -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

test:
	@echo "Testing..."
	@$(JR) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(TESTER_CLASS)

build: clean-build
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -cp "$(LIB_DIR)/*" -d "$(BUILD_DIR)/" \
	 -sourcepath "$(SRC_DIR)/" "$(SRC_DIR)/$(PLAYER_FILE)" "$(SRC_DIR)/$(TESTER_FILE)"

build-debug: clean-build
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -g -cp "$(LIB_DIR)/*" -d "$(BUILD_DIR)/" -sourcepath "$(SRC_DIR)/" \
	 "$(SRC_DIR)/$(PLAYER_FILE)" "$(SRC_DIR)/$(PLAYER_FILE)" "$(SRC_DIR)/$(TESTER_FILE)"

docs: clean-docs
	@$(JD) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" -d "$(DOCS_DIR)/" -sourcepath \
	 "$(SRC_DIR)/" -subpackages $(DOCS_PACKAGE)

clean: clean-docs clean-build

clean-build:
	@$(RM) -rf $(BUILD_DIR)

clean-docs:
	@$(RM) -rf $(DOCS_DIR) 
