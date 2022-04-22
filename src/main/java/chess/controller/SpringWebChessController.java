package chess.controller;

import chess.domain.dto.MoveRequestDto;
import chess.domain.dto.ResponseDto;
import chess.domain.game.Status;
import chess.service.ChessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<ResponseDto> start() {
        try {
            chessService.start();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto("정상적으로 시작되지 않았습니다."));
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/play")
    public String play(Model model) {
        if (chessService.checkStatus(Status.END)) {
            return "redirect:result";
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

    @GetMapping("/status")
    public String status(Model model) {
        if (chessService.checkStatus(Status.PLAYING)) {
            model.addAttribute("play", true);
            model.addAttribute("status", chessService.status());
            model.addAttribute("board", chessService.currentBoardForUI());
            return "game";
        }
        return "redirect:/end";
    }

    @PostMapping("/save")
    public String save() {
        if (chessService.checkStatus(Status.PLAYING)) {
            chessService.save();
        }
        return "redirect:/play";
    }

    @GetMapping("/end")
    public ResponseEntity<ResponseDto> end() {
        try {
            chessService.end();
        } catch (IllegalArgumentException | SQLException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(e.getMessage()));
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/result")
    public String result(Model model) throws SQLException {
        chessService.end();
        model.addAttribute("play", true);
        model.addAttribute("status", chessService.status());
        model.addAttribute("board", chessService.currentBoardForUI());
        model.addAttribute("winner", chessService.findWinner());
        return "game";
    }
}
