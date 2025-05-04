package com.ponggame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class PongGame extends ApplicationAdapter {
    private SpriteBatch batch;

    // Paddle Attributes
    private static final float PADDLE_WIDTH = 15;
    private static final float PADDLE_HEIGHT = 100;
    private static final float PADDLE_SPEED = 500;
    private static final float PADDLE_OFFSET = 20;

    // Ball Attributes
    private static final float BALL_SIZE = 15;
    private static final float INITIAL_BALL_SPEED = 300;
    private static final float BALL_SPEED_INCREASE = 1.05f;
    private static final float MAX_BALL_SPEED = 600;

    // Court Net
    private static final float CENTER_LINE_WIDTH = 4;
    private static final float CENTER_LINE_SEGMENT_HEIGHT = 20;
    private static final float CENTER_LINE_GAP = 10;

    // Winning Score
    private static final int WINNING_SCORE = 5;

    private float player1Y;
    private float player2Y;
    private int player1Score;
    private int player2Score;
    private float ballX;
    private float ballY;
    private float ballSpeedX;
    private float ballSpeedY;
    private long lastScoreTime;

    // Game State
    private enum GameState {
        PLAYING,
        SERVING,
        GAME_OVER
    }
    private GameState gameState;
    private boolean serveToPlayer1;
    private boolean isPaused;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Sound paddleHitSound;
    private Sound wallBounceSound;
    private Sound scoreSound;
    private Sound roundEndSound;
    private Sound gameStartSound;
    private Sound pauseSound;
    private Sound resumeSound;
    private Music backgroundMusic;

    private void resetBall() {
        ballX = (float) Gdx.graphics.getWidth() / 2 - BALL_SIZE / 2;
        ballY = (float) Gdx.graphics.getHeight() / 2 - BALL_SIZE / 2;
        ballSpeedX = 0;
        ballSpeedY = 0;
        lastScoreTime = TimeUtils.millis();
        gameState = GameState.SERVING;
    }

    private void resetGame() {
        player1Y = (float) Gdx.graphics.getHeight() / 2 - PADDLE_HEIGHT / 2;
        player2Y = (float) Gdx.graphics.getHeight() / 2 - PADDLE_HEIGHT / 2;
        player1Score = 0;
        player2Score = 0;
        serveToPlayer1 = MathUtils.randomBoolean();
        resetBall();
        isPaused = false;
    }

    private void handleInput(float delta) {
// Toggle pause with ESCAPE key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            if(isPaused){
                pauseSound.play();
            }else{
                resumeSound.play();
            }
        }
// If game is paused, only allow unpausing
        if (isPaused && !Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return;
        }
// Restart game if over
        if (gameState == GameState.GAME_OVER && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            gameStartSound.play();
            return;
        }
// Player 1 controls (W/S keys)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player1Y += PADDLE_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player1Y -= PADDLE_SPEED * delta;
        }
// Player 2 controls (Up/Down arrows)
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player2Y += PADDLE_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player2Y -= PADDLE_SPEED * delta;
        }
// Keep paddles on screen
        player1Y = MathUtils.clamp(player1Y, 0, Gdx.graphics.getHeight() - PADDLE_HEIGHT);
        player2Y = MathUtils.clamp(player2Y, 0, Gdx.graphics.getHeight() - PADDLE_HEIGHT);
// Space to serve
        if (gameState == GameState.SERVING && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            serveBall();
        }
    }

    private void serveBall() {
        gameState = GameState.PLAYING;
        float angle;
// Randomly choose one of the three allowed angle ranges
        int rangeChoice = MathUtils.random(0, 2);
        switch (rangeChoice) {
            case 0: // 0-45 degrees
                angle = MathUtils.random(0f, 45f);
                break;
            case 1: // 135-225 degrees
                angle = MathUtils.random(135f, 225f);
                break;
            case 2: // 315-360 degrees
                angle = MathUtils.random(315f, 360f);
                break;
            default: // Should never happen
                angle = MathUtils.random(0f, 360f);
        }
// If serving to player 1, reverse the angle
        if (serveToPlayer1) {
            angle = (angle + 180f) % 360f;
        }
        ballSpeedX = INITIAL_BALL_SPEED * MathUtils.cosDeg(angle);
        ballSpeedY = INITIAL_BALL_SPEED * MathUtils.sinDeg(angle);
        paddleHitSound.play();
    }

    private void update(float delta) {
        if (gameState != GameState.PLAYING) {
            return;
        }
// Update ball position
        ballX += ballSpeedX * delta;
        ballY += ballSpeedY * delta;
// Ball collision with top and bottom
        if (ballY < 0 || ballY > Gdx.graphics.getHeight() - BALL_SIZE) {
            ballSpeedY *= -1;
            ballY = MathUtils.clamp(ballY, 0, Gdx.graphics.getHeight() - BALL_SIZE);
            wallBounceSound.play(0.5f);
        }
// Ball collision with paddles
// Player 1 paddle (left)
        if (ballX < PADDLE_OFFSET + PADDLE_WIDTH &&
            ballY + BALL_SIZE > player1Y &&
            ballY < player1Y + PADDLE_HEIGHT) {
// Calculate angle based on where ball hits paddle
            float hitPosition = (ballY - player1Y) / PADDLE_HEIGHT;
            float angle = MathUtils.map(0, 1, -45, 45, hitPosition);
            float currentSpeed = (float) Math.sqrt(ballSpeedX * ballSpeedX + ballSpeedY * ballSpeedY);
            currentSpeed = Math.min(currentSpeed * BALL_SPEED_INCREASE, MAX_BALL_SPEED);
            ballSpeedX = currentSpeed * MathUtils.cosDeg(angle);
            ballSpeedY = currentSpeed * MathUtils.sinDeg(angle);
            ballX = PADDLE_OFFSET + PADDLE_WIDTH; // Prevent sticking
            paddleHitSound.play();
        }
// Player 2 paddle (right)
        if (ballX > Gdx.graphics.getWidth() - PADDLE_OFFSET - PADDLE_WIDTH - BALL_SIZE &&
            ballY + BALL_SIZE > player2Y &&
            ballY < player2Y + PADDLE_HEIGHT) {
            float hitPosition = (ballY - player2Y) / PADDLE_HEIGHT;
            float angle = MathUtils.map(0, 1, 225, 135, hitPosition); // Opposite direction
            float currentSpeed = (float) Math.sqrt(ballSpeedX * ballSpeedX + ballSpeedY * ballSpeedY);
            currentSpeed = Math.min(currentSpeed * BALL_SPEED_INCREASE, MAX_BALL_SPEED);
            ballSpeedX = currentSpeed * MathUtils.cosDeg(angle);
            ballSpeedY = currentSpeed * MathUtils.sinDeg(angle);
            ballX = Gdx.graphics.getWidth() - PADDLE_OFFSET - PADDLE_WIDTH - BALL_SIZE;
            paddleHitSound.play();
        }
// Ball out of bounds (scoring)
        if (ballX < 0) {
            player2Score++;
            serveToPlayer1 = true;
            scoreSound.play();
            resetBall();
        }
        if (ballX > Gdx.graphics.getWidth()) {
            player1Score++;
            serveToPlayer1 = false;
            scoreSound.play();
            resetBall();
        }
// Check for winner
        if (player1Score >= WINNING_SCORE || player2Score >= WINNING_SCORE) {
            gameState = GameState.GAME_OVER;
            roundEndSound.play();
        }
    }

    private void draw() {
// Draw game objects
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
// Draw paddles
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(PADDLE_OFFSET, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT); // Player 1
        shapeRenderer.rect(Gdx.graphics.getWidth() - PADDLE_OFFSET - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT); // Player 2
// Draw ball if not serving or during blink phase
        if (gameState != GameState.SERVING || (TimeUtils.timeSinceMillis(lastScoreTime) / 200) % 2 == 0) {
            shapeRenderer.rect(ballX, ballY, BALL_SIZE, BALL_SIZE);
        }
// Draw center line
        shapeRenderer.setColor(Color.GRAY);
        for (int i = 0; i < Gdx.graphics.getHeight(); i += CENTER_LINE_SEGMENT_HEIGHT + CENTER_LINE_GAP) {
            shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - CENTER_LINE_WIDTH / 2, i, CENTER_LINE_WIDTH, CENTER_LINE_SEGMENT_HEIGHT);
        }
        shapeRenderer.end();
// Draw scores and messages
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf(player1Score), Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() - 20);
        font.draw(batch, String.valueOf(player2Score), Gdx.graphics.getWidth() / 2 + 30, Gdx.graphics.getHeight() - 20);
// Draw game messages
        if (isPaused) {
            font.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 50);
            font.draw(batch, "Press ESC to resume", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 50);
        } else if (gameState == GameState.SERVING) {
            font.draw(batch, "Press SPACE to serve", Gdx.graphics.getWidth() / 2 - 100, 50);
        } else if (gameState == GameState.GAME_OVER) {
            String winner = player1Score >= WINNING_SCORE ? "Player 1 Wins!" : "Player 2 Wins!";
            font.draw(batch, winner, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
            font.draw(batch, "Press R to restart", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 50);
        }
        batch.end();
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        batch = new SpriteBatch();
// Make font larger
        font.getData().setScale(2);
// Load sound effects
        paddleHitSound = Gdx.audio.newSound(Gdx.files.internal("paddle_hit.ogg"));
        wallBounceSound = Gdx.audio.newSound(Gdx.files.internal("wall_bounce.ogg"));
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("score.ogg"));
        roundEndSound = Gdx.audio.newSound(Gdx.files.internal("round_end.ogg"));
        gameStartSound = Gdx.audio.newSound(Gdx.files.internal("game_start.ogg"));
        pauseSound = Gdx.audio.newSound(Gdx.files.internal("pause.ogg"));
        resumeSound = Gdx.audio.newSound(Gdx.files.internal("resume.ogg"));
// Load and configure background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.8f); // Lower volume for background music
        resetGame();
// Play game start sound
        gameStartSound.play();
// Start background music
        backgroundMusic.play();
    }

    @Override
    public void render() {
// Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
// Handle input
        handleInput(Gdx.graphics.getDeltaTime());
// Update game state if not paused
        if (!isPaused) {
            update(Gdx.graphics.getDeltaTime());
        }
// Draw game objects
        draw();
    }
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        batch.dispose();
// Dispose sound effects
        paddleHitSound.dispose();
        wallBounceSound.dispose();
        scoreSound.dispose();
        roundEndSound.dispose();
        gameStartSound.dispose();
// Dispose background music
        backgroundMusic.dispose();
    }
}
