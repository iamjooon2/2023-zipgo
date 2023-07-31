import { useState } from 'react';
import styled, { css } from 'styled-components';

import VerticalDotsIcon from '@/assets/svg/vertical_dots_icon.svg';
import StarRatingDisplay from '@/components/@common/StarRating/StarRatingDisplay/StartRatingDisplay';
import {
  COMMENT_VISIABLE_LINE_LIMIT,
  PROFILE_DEFAULT_IMG_URL,
  REACTIONS,
} from '@/constants/review';
import { Review } from '@/types/review/client';

interface ReviewItemProps extends Review {}

const ReviewItem = (reviewItemProps: ReviewItemProps) => {
  const {
    profileImageUrl = PROFILE_DEFAULT_IMG_URL,
    reviewerName,
    rating,
    date,
    tastePreference,
    stoolCondition,
    adverseReactions,
    comment,
  } = reviewItemProps;

  const [isCommentExpanded, setIsCommentExpanded] = useState(false);

  return (
    <div>
      <ReviewHeader>
        <ReviewerImageWrapper>
          <ReviewerImage src={profileImageUrl} alt={`${reviewerName} 프로필`} />
        </ReviewerImageWrapper>
        <ReviewerName>{reviewerName}</ReviewerName>
        <ReviewEditButton type="button">
          <img src={VerticalDotsIcon} alt="" />
        </ReviewEditButton>
      </ReviewHeader>
      <RatingContainer>
        <StarRatingDisplay rating={rating} size="small" />
        <TimeStamp>{date}</TimeStamp>
      </RatingContainer>
      <Reactions>
        <Reaction key={REACTIONS.TASTE_PREFERENCE}>
          <ReactionTitle>{REACTIONS.TASTE_PREFERENCE}</ReactionTitle>
          <ReactionContent>{tastePreference}</ReactionContent>
        </Reaction>
        <Reaction key={REACTIONS.STOOL_CONDITION}>
          <ReactionTitle>{REACTIONS.STOOL_CONDITION}</ReactionTitle>
          <ReactionContent>{stoolCondition}</ReactionContent>
        </Reaction>
        <Reaction key={REACTIONS.ADVERSE_REACTION}>
          <ReactionTitle>{REACTIONS.ADVERSE_REACTION}</ReactionTitle>
          <ReactionContent>
            {Boolean(adverseReactions.length) ? adverseReactions.join(', ') : REACTIONS.NOTHING}
          </ReactionContent>
        </Reaction>
      </Reactions>
      <Comment isExpanded={isCommentExpanded}>{comment}</Comment>
      {!isCommentExpanded && comment.length > COMMENT_VISIABLE_LINE_LIMIT && (
        <ShowMoreButton
          onClick={() => setIsCommentExpanded(prevIsExpanded => !prevIsExpanded)}
          type="button"
        >
          ..더보기
        </ShowMoreButton>
      )}
    </div>
  );
};

export default ReviewItem;

const ReviewHeader = styled.div`
  position: relative;

  display: flex;
  align-items: center;

  height: 5rem;
  margin-bottom: 1.6rem;
`;

const ReviewerImageWrapper = styled.div`
  position: relative;

  overflow: hidden;

  width: 5rem;
  height: 5rem;
  margin-right: 0.8rem;

  border-radius: 50%;
`;

const ReviewerImage = styled.img`
  position: absolute;
  top: 0;
  left: 0;

  width: 100%;
  height: 100%;

  object-fit: cover;
`;

const ReviewerName = styled.p`
  font-size: 1.6rem;
  font-weight: bold;
  color: ${({ theme }) => theme.color.grey500};
`;

const ReviewEditButton = styled.button`
  all: unset;

  cursor: pointer;

  position: absolute;
  right: 0;
`;

const RatingContainer = styled.div`
  display: flex;
  gap: 0.8rem;
  align-items: center;

  margin-bottom: 1.2rem;
`;

const TimeStamp = styled.span`
  font-size: 1.1rem;
  color: ${({ theme }) => theme.color.grey400};
`;

const Reactions = styled.ul`
  all: unset;

  display: flex;
  flex-direction: column;
  gap: 0.4rem;

  margin-bottom: 1.6rem;
`;

const Reaction = styled.li`
  all: unset;
`;

const ReactionTitle = styled.span`
  margin-right: 0.8rem;

  font-size: 1.4rem;
  color: ${({ theme }) => theme.color.grey400};
`;

const ReactionContent = styled.span`
  font-size: 1.4rem;
  color: ${({ theme }) => theme.color.grey700};
`;

const Comment = styled.p<{ isExpanded: boolean }>`
  max-height: 10rem;
  margin-bottom: 0.4rem;

  font-size: 1.3rem;
  color: ${({ theme }) => theme.color.grey500};

  ${({ isExpanded }) => {
    if (isExpanded) {
      return css`
        overflow: 'visible';
        display: block;
      `;
    }

    return css`
      overflow: hidden;
      display: -webkit-box;

      text-overflow: ellipsis;
      word-break: break-word;

      -webkit-box-orient: vertical;
      -webkit-line-clamp: 5;
    `;
  }};
`;

const ShowMoreButton = styled.button`
  all: unset;

  cursor: pointer;

  font-size: 1.3rem;
  color: ${({ theme }) => theme.color.grey300};
`;