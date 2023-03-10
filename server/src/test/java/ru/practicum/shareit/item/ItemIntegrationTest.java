package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ObjectMaker.makeItemDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:server/src/main/resources/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
public class ItemIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public ItemIntegrationTest() {
    }

    @Test
    @Order(1)
    public void itemCreateTest() throws Exception {
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(2)
    public void itemCreateWithoutAuthTest() throws Exception {
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(3)
    public void itemCreateWrongUserTest() throws Exception {
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 100)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void itemCreateWithoutAvailableTest() throws Exception {
        ItemDto itemDto = makeItemDto("Отвертка", "Аккумуляторная отвертка", null);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void itemCreateWithEmptyNameTest() throws Exception {
        ItemDto itemDto = makeItemDto("", "Аккумуляторная отвертка", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void itemCreateWithEmptyDescriptionTest() throws Exception {
        ItemDto itemDto = makeItemDto("Отвертка", null, true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    public void itemUpdateTest() throws Exception {
        ItemDto itemDto = makeItemDto(1, "Дрель+", "Аккумуляторная дрель", false);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(8)
    public void itemUpdateWithoutAuthTest() throws Exception {
        ItemDto itemDto = makeItemDto(1, "Дрель", "Простая дрель", false);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(9)
    public void itemUpdateWithOtherUserTest() throws Exception {
        ItemDto itemDto = makeItemDto("Дрель", "Простая дрель", false);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void itemUpdateAvailableTest() throws Exception {
        ItemDto itemDto = makeItemDto(null, null, true);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель+")))
                .andExpect(jsonPath("$.description", is("Аккумуляторная дрель")))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(11)
    public void itemUpdateDescriptionTest() throws Exception {
        ItemDto itemDto = makeItemDto(null, "Аккумуляторная дрель + аккумулятор", null);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель+")))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @Order(12)
    public void itemUpdateNameTest() throws Exception {
        ItemDto itemDto = makeItemDto("Аккумуляторная дрель", null, null);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is("Аккумуляторная дрель + аккумулятор")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @Order(13)
    public void itemGetTest() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        mvc.perform(get("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(14)
    public void itemGetFromOtherUserTest() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        mvc.perform(get("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(15)
    public void itemGetUnknownTest() throws Exception {
        mvc.perform(get("/items/100")
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(16)
    public void itemCreate2Test() throws Exception {
        ItemDto itemDto = makeItemDto("Отвертка", "Аккумуляторная отвертка", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(17)
    public void itemCreate3Test() throws Exception {
        ItemDto itemDto = makeItemDto("Клей Момент", "Тюбик суперклея марки Момент", true);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(18)
    public void itemGetAllForUser1Test() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(19)
    public void itemGetAllForUser4Test() throws Exception {
        ItemDto itemDto2 = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
        ItemDto itemDto3 = makeItemDto(3L, "Клей Момент", "Тюбик суперклея марки Момент", true);
        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDto3.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto3.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto3.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto3.getAvailable())));
    }

    @Test
    @Order(20)
    public void itemSearchTest() throws Exception {
        ItemDto itemDto1 = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        ItemDto itemDto2 = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
        mvc.perform(get("/items/search")
                        .param("text", "аккУМУляторная")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())));
    }

    @Test
    @Order(21)
    public void itemUpdateAvailable2Test() throws Exception {
        ItemDto itemDto = makeItemDto(null, null, false);
        mvc.perform(patch("/items/2")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.name", is("Отвертка")))
                .andExpect(jsonPath("$.description", is("Аккумуляторная отвертка")))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(22)
    public void itemSearch2Test() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        mvc.perform(get("/items/search")
                        .param("text", "дРелЬ")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(23)
    public void itemSearch3Test() throws Exception {
        ItemDto itemDto = makeItemDto(1L, "Аккумуляторная дрель", "Аккумуляторная дрель + аккумулятор", true);
        mvc.perform(get("/items/search")
                        .param("text", "аккУМУляторная")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(24)
    public void itemUpdateAvailable3Test() throws Exception {
        ItemDto itemDto = makeItemDto(null, null, true);
        mvc.perform(patch("/items/2")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 4)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.name", is("Отвертка")))
                .andExpect(jsonPath("$.description", is("Аккумуляторная отвертка")))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(25)
    public void itemSearch4Test() throws Exception {
        ItemDto itemDto = makeItemDto(2L, "Отвертка", "Аккумуляторная отвертка", true);
        mvc.perform(get("/items/search")
                        .param("text", "оТверТ")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @Order(26)
    public void itemSearchEmptyTest() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}