
use rsvp;

CREATE TABLE `posts` (
  `post_id` VARCHAR(8) NOT NULL,
  `comments` MEDIUMTEXT NULL,
  `picture` MEDIUMBLOB NULL,
  PRIMARY KEY (`post_id`));
  
  
select * from posts ;
  
    
  
  
  
  