package io.github.teamfractal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Roboticon;
import io.github.teamfractal.entity.enums.PurchaseStatus;
import io.github.teamfractal.entity.enums.ResourceType;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.teamfractal.util.TTFont;

import java.util.ArrayList;

/**
 * Created by Joseph on 08/02/2017.
 */
public class RoboticonMarketActors extends Table {
    private static final Texture no_cust_texture;
    private static final Texture energy_texture;
    private static final Texture ore_texture;
    private static final Texture food_texture;
    private static final Texture no_robotic_texture;

    static {
        no_cust_texture = new Texture(Gdx.files.internal("roboticon_images/Robot.png"));
        energy_texture = new Texture(Gdx.files.internal("roboticon_images/Robot_energy.png"));
        ore_texture = new Texture(Gdx.files.internal("roboticon_images/Robot_ore.png"));
        food_texture = new Texture(Gdx.files.internal("roboticon_images/Robot_Food.png"));
        no_robotic_texture = new Texture(Gdx.files.internal("roboticon_images/No_Roboticons.png"));
    }

    private RoboticonQuest game;

    public ArrayList<Roboticon> roboticons = new ArrayList<Roboticon>();
    private Texture roboticonTexture;
    private Image roboticonImage;

    private Table purchaseTable;
    private Table selectionTable;
    private Table upgradeTable;

    private Integer roboticonPurchaseAmount;
    private Label roboticonPurchaseAmountLabel;

    private TextButton roboticonSubButton;
    private TextButton roboticonAddButton;
    private TextButton roboticonPurchaseButton;

    private Integer selectedRoboticonIndex;
    private Label selectedRoboticonIDLabel;
    private TextButton moveLeftInventoryButton;
    private TextButton moveRightInventoryButton;

    private SelectBox<String> customisationDropDown;
    private TextButton customisationPurchaseButton;

    private TextButton exitButton;

    private TTFont montserratRegular;
    private TTFont montserratLight;

    public RoboticonMarketActors(RoboticonQuest game) {
        this.game = game;

        this.roboticonImage = new Image();

        montserratRegular = new TTFont(Gdx.files.internal("font/MontserratRegular.ttf"));
        montserratLight = new TTFont(Gdx.files.internal("font/MontserratLight.ttf"));
        montserratRegular.setSize(24);
        montserratLight.setSize(12);

        constructInterface();
    }

    private void constructInterface() {
        purchaseTable = new Table();
        selectionTable = new Table();
        upgradeTable = new Table();

        roboticonPurchaseAmount = 1;

        constructLabels();
        constructButtons();

        purchaseTable.add(new Label("PURCHASE ROBOTICONS", new Label.LabelStyle(montserratRegular.font(), Color.WHITE))).colspan(4);

        purchaseTable.row();
        purchaseTable.add(roboticonSubButton).width(25);
        purchaseTable.add(roboticonPurchaseAmountLabel).width(50).expandX();
        purchaseTable.add(roboticonAddButton).width(25);
        purchaseTable.add(roboticonPurchaseButton).align(Align.right).width(200).padLeft(15);

        add(purchaseTable).padBottom(35);
        row();

        selectionTable.add(new Label("CUSTOMISE ROBOTICONS", new Label.LabelStyle(montserratRegular.font(), Color.WHITE))).colspan(3);

        selectionTable.row();
        selectionTable.add(moveLeftInventoryButton).width(25);
        selectionTable.add(roboticonImage).expandX();
        selectionTable.add(moveRightInventoryButton).width(25);

        selectionTable.row();
        selectionTable.add(selectedRoboticonIDLabel).colspan(3);

        add(selectionTable).padBottom(10);
        row();

        customisationDropDown = new SelectBox<String>(game.skin);
        customisationDropDown.setItems(new String[]{"Energy Generation", "Ore Mining", "Food Farming"});
        upgradeTable.add(customisationDropDown).expandX().padRight(15);
        upgradeTable.add(customisationPurchaseButton).width(164);

        add(upgradeTable).padBottom(35);
        row();

        add(exitButton).expandX().width(320);
    }

    private void constructLabels() {
        roboticonPurchaseAmountLabel = new Label(roboticonPurchaseAmount + "/" +  game.market.getResource(ResourceType.ROBOTICON), game.skin);
        roboticonPurchaseAmountLabel.setAlignment(Align.center);

        selectedRoboticonIDLabel = new Label("", game.skin);
        selectedRoboticonIDLabel.setAlignment(Align.center);
    }

    private void constructButtons() {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = montserratLight.font();
        buttonStyle.fontColor = Color.WHITE;

        //Increases number of roboticons to be purchased
        roboticonAddButton = new TextButton("+", game.skin);
        roboticonAddButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addRoboticonFunction();
            }
        });

        //Decreases number of roboticons to be purchased
        roboticonSubButton = new TextButton("-", game.skin);
        roboticonSubButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                subRoboticonFunction();
            }
        });

        //Purchases the specified number of roboticons
        roboticonPurchaseButton = new TextButton("[PRICE: " + (game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) + "] PURCHASE", game.skin);
        roboticonPurchaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                purchaseRoboticonFunction();
            }
        });

        moveLeftInventoryButton = new TextButton("<", game.skin);
        moveLeftInventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex > 0) {
                    selectedRoboticonIndex--;
                    refreshCurrentlySelectedRoboticon();
                }
            }
        });

        moveRightInventoryButton = new TextButton(">", game.skin);
        moveRightInventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex < roboticons.size() - 1) {
                    selectedRoboticonIndex++;
                    refreshCurrentlySelectedRoboticon();
                }
            }
        });

        customisationPurchaseButton = new TextButton("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE", game.skin);
        customisationPurchaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex == -1) {
                    return;
                }

                ResourceType resource = null;

                switch(customisationDropDown.getSelectedIndex()) {
                    case (0):
                        resource = ResourceType.ENERGY;
                        break;
                    case (1):
                        resource = ResourceType.ORE;
                        break;
                    case (2):
                        resource = ResourceType.FOOD;
                        break;
                }

                purchaseCustomisationFunction(resource, selectedRoboticonIndex);
            }
        });

        exitButton = new TextButton("EXIT ROBOTICON SHOP", game.skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.nextPhase();
            }
        });
    }

    public void refreshRoboticonShop() {
        roboticonPurchaseAmountLabel.setText(roboticonPurchaseAmount + "/" + game.market.getResource(ResourceType.ROBOTICON));
        roboticonPurchaseButton.setText("[PRICE: " + (game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) + "] PURCHASE");

        if (game.getPlayer().getMoney() >= game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) {
            roboticonPurchaseButton.setTouchable(Touchable.enabled);
        } else {
            roboticonPurchaseButton.setTouchable(Touchable.disabled);
        }
	}

    public void addRoboticonFunction() {
        if (roboticonPurchaseAmount < game.market.getResource(ResourceType.ROBOTICON)) {
            roboticonPurchaseAmount += 1;
            refreshRoboticonShop();
        }
    }

    public void subRoboticonFunction() {
        if (roboticonPurchaseAmount > 1) {
            roboticonPurchaseAmount -= 1;
            refreshRoboticonShop();
        }
    }

    public void purchaseCustomisationFunction(ResourceType resource, int index) {
        if (game.getPlayer().purchaseCustomisationFromMarket(resource, roboticons.get(index), game.market) == PurchaseStatus.Success) {
            widgetUpdate();
            game.gameScreen.getActors().textUpdate();

            customisationPurchaseButton.setText("CUSTOMISED");
            customisationPurchaseButton.setTouchable(Touchable.disabled);
        }
    }

    public void purchaseRoboticonFunction() {
        if (game.getPlayer().purchaseRoboticonsFromMarket(roboticonPurchaseAmount, game.market) == PurchaseStatus.Success) {
            roboticonPurchaseAmount = 1;
            refreshRoboticonShop();
            widgetUpdate();
            game.gameScreen.getActors().textUpdate();
        }
    }

    public void widgetUpdate() {
        roboticons.clear();
        for (Roboticon r : game.getPlayer().getRoboticons()) {
            if (!r.isInstalled()) {
                roboticons.add(r);
            }
        }

        if (roboticons.size() == 0) {
            selectedRoboticonIndex = -1;
        } else if (selectedRoboticonIndex == -1) {
            selectedRoboticonIndex = 0;
        }

        refreshCurrentlySelectedRoboticon();
    }

    private void refreshCurrentlySelectedRoboticon() {
        if (selectedRoboticonIndex != -1) {
            ResourceType roboticonType = roboticons.get(selectedRoboticonIndex).getCustomisation();

            switch (roboticonType) {
                case Unknown:
                    roboticonTexture = no_cust_texture;
                    break;
                case ENERGY:
                    roboticonTexture = energy_texture;
                    break;
                case ORE:
                    roboticonTexture = ore_texture;
                    break;
                case FOOD:
                    roboticonTexture = food_texture;
                    break;
                default:
                    break;
            }

            this.selectedRoboticonIDLabel.setText("[" + (selectedRoboticonIndex + 1) + "/" + roboticons.size() + "] ISSUE NUMBER: " + padZero(roboticons.get(selectedRoboticonIndex).getID(), 4));

            if (roboticonType == ResourceType.Unknown) {
                customisationPurchaseButton.setText("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE");
                customisationPurchaseButton.setTouchable(Touchable.enabled);
            } else {
                customisationPurchaseButton.setText("CUSTOMISED");
                customisationPurchaseButton.setTouchable(Touchable.disabled);
            }
        } else {
            roboticonTexture = no_robotic_texture;
            this.selectedRoboticonIDLabel.setText("ISSUE NUMBER: ####");

            customisationPurchaseButton.setText("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE");
            customisationPurchaseButton.setTouchable(Touchable.disabled);
        }

        roboticonImage.setDrawable(new TextureRegionDrawable(new TextureRegion(roboticonTexture)));
    }


    public String padZero(int number, int length) {
        String s = "" + number;
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    public int selectedRoboticonIndex() {
        return selectedRoboticonIndex;
    }

    public void updateUpgradePriceLabels() {

    }
}
