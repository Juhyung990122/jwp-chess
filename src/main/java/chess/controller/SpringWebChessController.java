package chess.controller;

import chess.domain.dto.MoveRequestDto;
import chess.domain.dto.ResponseDto;
import chess.domain.dto.ResponseDto1;
import chess.domain.game.Status;
import chess.service.ChessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
public class SpringWebChessController {

    private final ChessService chessService;

    public SpringWebChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @GetMapping("/")
    public String index() {
        return "game";
    }

    @PostMapping("/start")
    public ResponseEntity<ResponseDto1> start() {
        return ResponseEntity
                .ok()
                .body(chessService.start());
    }

    @GetMapping("/play")
    public String play(Model model) {
        if (chessService.checkStatus(Status.END)) {
            return "redirect:game";
        }
        model.addAttribute("play", true);
        model.addAttribute("board", chessService.currentBoardForUI());
        return "game";
    }

    @PostMapping(value = "/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> move(@RequestBody MoveRequestDto moveRequestDto) throws SQLException {
        try {
            chessService.move(moveRequestDto.getSource(), moveRequestDto.getTarget());
            if (chessService.checkStatus(Status.END)) {
                chessService.end();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(e.getMessage()));
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
