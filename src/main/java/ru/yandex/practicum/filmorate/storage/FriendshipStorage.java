package ru.yandex.practicum.filmorate.storage;

public interface FriendshipStorage {
   void addFriend(long userId, long friendId);

   void deleteFriend(long userId, long friendId);
}
