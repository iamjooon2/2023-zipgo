import { useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { styled } from 'styled-components';

import Input from '@/components/@common/Input/Input';
import { PET_PROFILE_ADDITION_STEP } from '@/constants/petProfile';
import { usePetProfileAddition } from '@/hooks/petProfile/usePetProfileAddition';
import { PetAdditionOutletContextProps } from '@/types/petProfile/client';

const PetProfileWeightAddition = () => {
  const { petProfile, isValidInput, onChangeWeight } = usePetProfileAddition();
  const { updateCurrentStep, updateIsValidStep } =
    useOutletContext<PetAdditionOutletContextProps>();

  useEffect(() => {
    updateIsValidStep(false);
    updateCurrentStep(PET_PROFILE_ADDITION_STEP.WEIGHT);
  }, []);

  return (
    <Container>
      <PetName>{petProfile.name}</PetName>
      <Title>의 몸무게를 입력해주세요.</Title>
      <InputLabel htmlFor="pet-weight">몸무게 입력</InputLabel>
      <WeightInputContainer>
        <Input
          id="pet-weight"
          type="text"
          required
          minLength={1}
          placeholder="예) 7.5"
          maxLength={5}
          isValid={isValidInput}
          onChange={onChangeWeight}
          design="underline"
          fontSize="1.3rem"
          inputMode="decimal"
        />
        <Kg>kg</Kg>
      </WeightInputContainer>
      <ErrorCaption>
        {isValidInput ? '' : '몸무게는 0kg초과, 100kg이하 소수점 첫째짜리까지 입력이 가능합니다.'}
      </ErrorCaption>
    </Container>
  );
};

export default PetProfileWeightAddition;

const Container = styled.div`
  margin-top: 4rem;
`;

const Title = styled.h2`
  display: inline-block;

  margin-bottom: 6rem;

  font-size: 2.4rem;
  font-weight: 700;
  line-height: 1.7rem;
  color: ${({ theme }) => theme.color.grey600};
  letter-spacing: -0.5px;
`;

const PetName = styled.span`
  font-size: 2.4rem;
  font-weight: 700;
  line-height: 1.7rem;
  color: ${({ theme }) => theme.color.primary};
  letter-spacing: -0.5px;
`;

const InputLabel = styled.label`
  display: block;

  margin-bottom: 1.6rem;

  font-size: 1.3rem;
  font-weight: 500;
  line-height: 1.7rem;
  color: ${({ theme }) => theme.color.grey600};
  letter-spacing: -0.5px;
`;

const WeightInputContainer = styled.div`
  position: relative;
`;

const ErrorCaption = styled.p`
  margin-top: 1rem;

  font-size: 1.3rem;
  font-weight: 500;
  line-height: 1.7rem;
  color: ${({ theme }) => theme.color.warning};
  letter-spacing: -0.5px;
`;

const Kg = styled.p`
  position: absolute;
  top: 1.2rem;
  right: 1.2rem;

  font-size: 1.3rem;
  font-weight: 500;
  line-height: 1.7rem;
  color: ${({ theme }) => theme.color.grey600};
  letter-spacing: -0.5px;
`;