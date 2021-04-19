JC = javac
JR = java

LIB_DIR = lib
SRC_DIR  = src
BUILD_DIR  = build

MAIN_CLASS = mnkgame.MNKGame
TESTER_CLASS = mnkgame.MNKPlayerTester
PLAYER_CLASS = monkey.MoNKey

PLAYER_FILE = monkey/MoNKey.java

MNK = 3 3 3
TEST = mnkgame.QuasiRandomPlayer -r 10 -v

run: build
	@echo "Running..."
	@$(JR) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

test: build
	@echo "Testing..."
	@$(JR) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(TESTER_CLASS) $(MNK) $(PLAYER_CLASS) $(TEST)

build:
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -cp "$(LIB_DIR)/*:" -d $(BUILD_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/$(PLAYER_FILE)

clean:
	@$(RM) -r $(BUILD_DIR)
