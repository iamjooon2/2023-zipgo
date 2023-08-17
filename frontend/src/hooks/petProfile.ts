import { ChangeEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import {
  FEMALE,
  MALE,
  MIXED_BREED,
  PET_AGE_MAX,
  PET_AGE_MIN,
  PET_PROFILE_ADDITION_STEP,
  STEP_PATH,
} from '@/constants/petProfile';
import { routerPath } from '@/router/routes';
import { Gender } from '@/types/petProfile/client';

export const usePetProfileStep = () => {
  const navigate = useNavigate();

  const [step, setStep] = useState(0);
  const [isValidStep, setIsValidStep] = useState(false);
  const [isMixedBreed, setIsMixedBreed] = useState(false);

  const totalStep = Object.values(PET_PROFILE_ADDITION_STEP).length;
  const isLastStep = step === totalStep;

  const goBack = (): void => navigate(routerPath.back);
  const goNext = () => {
    if (step === PET_PROFILE_ADDITION_STEP.BREED && !isMixedBreed) {
      navigate(STEP_PATH[step + 2]);
      return;
    }

    if (!isLastStep) navigate(STEP_PATH[step + 1]);
  };

  const updateIsMixedBreed = (isMixed: boolean) => setIsMixedBreed(isMixed);
  const updateCurrentStep = (step: number) => setStep(step);
  const updateIsValidStep = (isValid: boolean) => setIsValidStep(isValid);

  return {
    step,
    totalStep,
    isLastStep,
    isValidStep,
    updateIsMixedBreed,
    updateCurrentStep,
    updateIsValidStep,
    goBack,
    goNext,
  };
};

export const usePetProfileValidation = () => {
  const [isValidNameInput, setIsValidNameInput] = useState(true);
  const [isValidAgeSelect, setIsValidAgeSelect] = useState(true);
  const [isValidWeightInput, setIsValidWeightInput] = useState(true);

  const validateName = (e: ChangeEvent<HTMLInputElement>) => {
    const petName = e.target.value;

    if (isValidName(petName)) {
      setIsValidNameInput(true);
      return true;
    }

    setIsValidNameInput(false);
    return false;
  };

  const validateAge = (e: ChangeEvent<HTMLSelectElement>) => {
    const petAge = Number(e.target.value);

    if (isValidAgeRange(petAge)) {
      setIsValidWeightInput(true);
      return true;
    }

    setIsValidAgeSelect(false);
    return false;
  };

  const validateWeight = (e: ChangeEvent<HTMLInputElement>) => {
    const petWeight = e.target.value;

    if (isValidWeight(petWeight)) {
      setIsValidWeightInput(true);
      return true;
    }

    setIsValidWeightInput(false);
    return false;
  };

  const isValidName = (name: string) => {
    const validNameCharacters = /^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,10}$/;

    return validNameCharacters.test(name);
  };

  const isValidAgeRange = (age: number) =>
    typeof age === 'number' && age >= PET_AGE_MIN && age <= PET_AGE_MAX;

  const isValidGender = (gender: string): gender is Gender => gender === MALE || gender === FEMALE;

  const isValidWeight = (weight: string) => {
    const validWeightCharacters = /^(?:100(?:\.0)?|\d{1,2}(?:\.\d)?)$/; // 100.0 또는 1~2자리 숫자.소수 첫째짜리 숫자

    return validWeightCharacters.test(weight);
  };

  const isMixedBreed = (breed: string) => breed === MIXED_BREED;

  return {
    isValidNameInput,
    isValidAgeSelect,
    isValidWeightInput,

    validateName,
    validateAge,
    validateWeight,
    isValidName,
    isValidAgeRange,
    isValidGender,
    isValidWeight,
    isMixedBreed,
  };
};