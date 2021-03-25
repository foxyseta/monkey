JC = javac
JR = java

LIB_DIR = lib
SRC_DIR  = src
BUILD_DIR  = build

MAIN_CLASS = mnkgame.MNKGame
PLAYER_CLASS = jenny.Jenny

PLAYER_FILE = jenny/Jenny.java

MNK = 3 3 3

run: build
	@$(JR) -cp "$(LIB_DIR)/*:$(BUILD_DIR)/" $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

build:
	@echo "Building..."
	@mkdir -p $(BUILD_DIR)
	@$(JC) -cp "$(LIB_DIR)/*:" -d $(BUILD_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/$(PLAYER_FILE)

clean:
	@$(RM) -r $(BUILD_DIR)
