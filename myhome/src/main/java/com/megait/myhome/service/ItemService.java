package com.megait.myhome.service;

import com.megait.myhome.domain.Album;
import com.megait.myhome.domain.Book;
import com.megait.myhome.domain.Item;
import com.megait.myhome.domain.Member;
import com.megait.myhome.repository.AlbumRepository;
import com.megait.myhome.repository.BookRepository;
import com.megait.myhome.repository.ItemRepository;
import com.megait.myhome.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final AlbumRepository albumRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void initBookItems() throws IOException {
        Resource resource = new ClassPathResource("book.CSV");
        List<Item> bookList= Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .stream().map(line->{
                    String[] split = line.split("\\|");
                    Book book = new Book();
                    book.setName(split[0]);
                    book.setImageUrl(split[1]);
                    book.setPrice(Integer.parseInt(split[2]));
                    return book;
                })
                .collect(Collectors.toList());

        itemRepository.saveAll(bookList);
    }

    @PostConstruct
    public void initAlbumItems() throws IOException {
        Resource resource = new ClassPathResource("album.CSV");
        List<Item> itemList= Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                .map(line->{
                    String[] split = line.split("\\|");
                    Album album = new Album();
                    album.setName(split[0]);
                    album.setImageUrl(split[1]);
                    album.setPrice(Integer.parseInt(split[2]));
                    return album;
                }).collect(Collectors.toList());

        itemRepository.saveAll(itemList);
    }

    public List<Book> getBooks(){
        return bookRepository.findAll();
    }

    public List<Album> getAlbums() {
        return albumRepository.findAll();
    }

    public Item getItem(Long id){
        Optional<Item> item = itemRepository.findById(id);

        return item.isEmpty() ? null : item.get();
    }

    public void processLike(Member member, Item item) {
        member.getLikes().add(item);
        item.setLiked(item.getLiked() + 1);
    }


    public void deleteLikes(Member member, List<Long> idList) {
        member = memberRepository.getOne(member.getId());
        member.getLikes().removeAll(itemRepository.findAllById(idList));
    }
}
