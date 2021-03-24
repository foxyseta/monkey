SRC_DIR  = src
BUILD_DIR  = build
MAIN_FILE = mnkgame/MNKGame.java
PLAYER_FILE = jenny/Jenny.java
MAIN_CLASS = mnkgame.MNKGame
PLAYER_CLASS = jenny.Jenny

JC = javac
JR = java

run: build
	@$(JR) -cp $(BUILD_DIR) $(MAIN_CLASS) $(MNK) $(PLAYER_CLASS)

build:
	@mkdir -p $(BUILD_DIR)
	@$(JC) -sourcepath $(SRC_DIR) -d $(BUILD_DIR) \
		$(SRC_DIR)/$(MAIN_FILE) $(SRC_DIR)/$(PLAYER_FILE)

clean:
	@$(RM) -r $(BUILD_DIR)
