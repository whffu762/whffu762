package tomato.classifier.repository.article;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import tomato.classifier.entity.Comment;
import tomato.classifier.repository.article.CommentCustomRepository;

import java.util.List;

import static tomato.classifier.entity.QComment.comment;

@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory queryFactory;

    public CommentCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.queryFactory = jpaQueryFactory;
    }

    public List<Comment> findByArticleId(Integer articleId){
        return queryFactory
                .select(comment)
                .from(comment)
                .where(comment.article.articleId.eq(articleId))
                .fetch();
    }

}
