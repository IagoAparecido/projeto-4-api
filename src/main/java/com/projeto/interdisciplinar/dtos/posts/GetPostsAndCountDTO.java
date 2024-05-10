package com.projeto.interdisciplinar.dtos.posts;

import java.util.List;

public record GetPostsAndCountDTO(List<PostsDTO> posts, int totalPages) {

}
