package chess.controller.web;

import chess.controller.web.dto.*;
import chess.domain.manager.ChessGameManager;
import chess.domain.manager.ChessGameManagerBundle;
import chess.service.ChessService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChessController {
    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @GetMapping("/games")
    public RunningGameResponseDto getGames() {
        ChessGameManagerBundle runningGames = chessService.findRunningGames();
        return new RunningGameResponseDto(runningGames.getIdAndNextTurn());
    }

    @GetMapping("/game/start")
    public ChessGameResponseDto gameStart() {
        return new ChessGameResponseDto(chessService.start());
    }

    @GetMapping("/games/{id:[\\d]+}/score")
    public ScoreResponseDto getScore(@PathVariable long id) {
        return new ScoreResponseDto(chessService.getStatistics(id));
    }

    @GetMapping("/games/{id:[\\d]+}/load")
    public ChessGameResponseDto loadGame(@PathVariable long id) {
        ChessGameManager load = chessService.findById(id);
        return new ChessGameResponseDto(load);
    }

    @PostMapping("/games/{id:[\\d]+}/move")
    public MoveResponseDto movePiece(@PathVariable long id, @RequestBody MoveRequestDto moveMessage) {
        chessService.move(id, moveMessage);
        return new MoveResponseDto(chessService.isEnd(id), chessService.nextColor(id));
    }
}
